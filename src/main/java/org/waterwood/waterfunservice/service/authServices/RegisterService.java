package org.waterwood.waterfunservice.service.authServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.repository.UserDatumRepo;
import org.waterwood.waterfunservice.service.EncryptedKeyService;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.DTO.request.RegisterRequest;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.entity.user.UserDatum;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.utils.security.EncryptionHelper;
import org.waterwood.waterfunservice.utils.security.PasswordUtil;
import org.waterwood.waterfunservice.utils.streamApi.AuthValidator;

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
        ApiResponse<LoginServiceResponse> result = AuthValidator.start()
                .validateUsername(request.getUsername())
                //.checkEmpty(request.getPassword(), ResponseCode.PASSWORD_EMPTY) allow password empty
                .checkEmpty(request.getSmsCode(), ResponseCode.SMS_CODE_EMPTY)
                .check(authService.getSmsCodeService().verifySmsCode(
                        request.getPhoneNumber(),uuid,request.getSmsCode()), ResponseCode.SMS_CODE_INCORRECT)
                .buildResult();
        if(! result.isSuccess()) return result;
        return userRepo.findByUsername(request.getUsername()).map(
                        user -> ApiResponse.<LoginServiceResponse>failure(ResponseCode.USER_NOT_FOUND))
                .orElseGet(() -> {
                    // Create a new user
                    User user = new User();
                    user.setUsername(request.getUsername());
                    user.setPasswordHash(PasswordUtil.encryptPassword(request.getPassword()));

                    // Random pick dek and encrypted
                    return encryptedKeyService.randomPickEncryptionKey().map(
                            key->{
                                // Create a new user datum
                                Long userId = user.getId();
                                UserDatum userDatum = new UserDatum();
                                userDatum.setId(userId);
                                userDatum.setEncryptionKeyId(key.getId());
                                try {
                                    userDatum.setEmail(EncryptionHelper.encryptField(request.getEmail(),key));
                                    userDatum.setPhone(EncryptionHelper.encryptField(request.getPhoneNumber(),key));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                userDatum.setPhoneVerified(true);
                                userRepo.save(user);
                                userDatumRepo.save(userDatum);
                                return authService.validateTokens(null,null,user);
                            }
                    ).orElse(ApiResponse.failure(ResponseCode.INTERNAL_SERVER_ERROR));
                });
    }
}
