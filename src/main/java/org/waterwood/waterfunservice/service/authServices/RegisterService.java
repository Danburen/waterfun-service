package org.waterwood.waterfunservice.service.authServices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;
import org.waterwood.waterfunservice.entity.user.AccountStatus;
import org.waterwood.waterfunservice.repository.UserDatumRepo;
import org.waterwood.waterfunservice.service.EncryptedKeyService;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.DTO.request.RegisterRequest;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.entity.user.UserDatum;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.utils.ValidateUtil;
import org.waterwood.waterfunservice.utils.security.*;
import org.waterwood.waterfunservice.utils.streamApi.AuthValidator;

@Slf4j
@Service
public class RegisterService {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserDatumRepo userDatumRepo;
    @Autowired
    private EncryptedKeyService encryptedKeyService;

    @Transactional
    public ApiResponse<LoginServiceResponse> register(RegisterRequest request, String uuid) {
        boolean isPasswordEmpty = request.getPassword() == null || request.getPassword().isEmpty();
        boolean isEmailEmpty = request.getEmail() == null || request.getEmail().isEmpty();
        ApiResponse<LoginServiceResponse> result = AuthValidator.start()
                .checkEmpty(request.getSmsCode(), ResponseCode.SMS_CODE_EMPTY)
                .checkEmpty(request.getUsername(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                .checkEmpty(request.getPhoneNumber(),ResponseCode.PHONE_NUMBER_EMPTY_OR_INVALID)
                .check(isPasswordEmpty || ValidateUtil.validateBasicPassword(request.getPassword()),
                        ResponseCode.PASSWORD_EMPTY_OR_INVALID)
                .check( isEmailEmpty || ValidateUtil.validateEmail(request.getEmail()),
                        ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID)
                .check(ValidateUtil.validateUsername(request.getUsername()),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                .check(ValidateUtil.validatePhone(request.getPhoneNumber()),ResponseCode.PHONE_NUMBER_EMPTY_OR_INVALID)
                .check(authService.getSmsCodeService().verifySmsCode(
                        request.getPhoneNumber(),uuid,request.getSmsCode()), ResponseCode.SMS_CODE_INCORRECT)
                .buildResult();
        if(! result.isSuccess()) return result;
        return userRepo.findByUsername(request.getUsername()).map(
                        user -> ApiResponse.<LoginServiceResponse>failure(ResponseCode.USER_ALREADY_EXISTS))
                .orElseGet(() -> {
                    // Create a new user
                    User user = new User();
                    user.setUsername(request.getUsername());
                    String password = request.getPassword();
                    if (isPasswordEmpty) password = PasswordUtil.generatePassword(12);
                    user.setPasswordHash(PasswordUtil.encryptPassword(password));
                    // Random pick dek and encrypted
                    return encryptedKeyService.pickEncryptionKeys(0,1).map(
                            keys->{
                                EncryptionDataKey encryptionKey = keys.get(0);
                                String hmacKey = keys.get(1).getEncryptedKey();
                                String phone = request.getPhoneNumber();
                                String email = request.getEmail();
                                // Create a new user datum
                                Long userId = user.getId();
                                UserDatum userDatum = new UserDatum();
                                userDatum.setUser(user);
                                userDatum.setId(userId);
                                userDatum.setEncryptionKeyId(keys.get(0).getId());
                                // Encrypt phone & email and save
                                if(! isEmailEmpty) {
                                    EncryptedData encryptEmailData = PartialEncryptionHelper.encryptEmail(email,encryptionKey);
                                    userDatum.setEmailEncrypted(encryptEmailData.encryptedValue());
                                    userDatum.setEmailDisplay(encryptEmailData.concatDisplay());
                                    userDatum.setEmailHash(HashUtil.calculateHmac(email,hmacKey));
                                }
                                EncryptedData encryptPhoneData = PartialEncryptionHelper.encryptPhone(phone,encryptionKey);userDatum.setPhoneEncrypted(encryptPhoneData.encryptedValue());
                                userDatum.setPhonePrefix(encryptPhoneData.displayPrefix());
                                userDatum.setPhoneHash(HashUtil.calculateHmac(phone,hmacKey));
                                userDatum.setPhoneVerified(true);
                                userDatumRepo.save(userDatum);
                                user.setAccountStatus(AccountStatus.ACTIVE);
                                userRepo.save(user);
                                return authService.validateTokens(null,null,user);
                            }
                    ).orElse(ApiResponse.failure(ResponseCode.INTERNAL_SERVER_ERROR));
                });
    }
}
