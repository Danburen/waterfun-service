package org.waterwood.waterfunservice.service.authServices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.ServiceResult;
import org.waterwood.waterfunservice.repository.UserDatumRepo;
import org.waterwood.waterfunservice.service.EncryptedKeyService;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.service.Impl.RSAJwtTokenService;
import org.waterwood.waterfunservice.service.dto.RefreshTokenPayload;
import org.waterwood.waterfunservice.utils.ValidateUtil;
import org.waterwood.waterfunservice.utils.security.HashUtil;
import org.waterwood.waterfunservice.utils.security.PartialEncryptionHelper;

@Slf4j
@Service
public class LoginService {
    private final UserRepository userRepo;
    private final AuthService authService;
    private final RSAJwtTokenService tokenService;
    private final UserDatumRepo userDatumRepo;
    private final EncryptedKeyService encryptedKeyService;
    private final CaptchaService captchaService;

    public LoginService(UserRepository ur, AuthService as, RSAJwtTokenService ts, CaptchaService cs, EncryptedKeyService edks, UserDatumRepo udr) {
        this.userRepo = ur;
        this.authService = as;
        this.tokenService = ts;
        this.captchaService = cs;
        this.encryptedKeyService = edks;
        this.userDatumRepo = udr;

    }

    public ServiceResult<LoginServiceResponse> verifyPasswordLogin(PwdLoginRequestBody requestBody, String captchaUUID) {
        return userRepo.findByUsername(requestBody.getUsername()).map(
                        user-> AuthValidator.start()
                                .checkEmpty(requestBody.getUsername(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                                .checkEmpty(requestBody.getPassword(), ResponseCode.PASSWORD_EMPTY_OR_INVALID)
                                .checkEmpty(requestBody.getCaptcha(), ResponseCode.CAPTCHA_EMPTY)
                                .checkEmpty(requestBody.getDeviceFp(),ResponseCode.DEVICE_FINGERPRINT_REQUIRED)
                                .check(captchaService.validateCaptcha(captchaUUID,requestBody.getCaptcha()), ResponseCode.CAPTCHA_INCORRECT)
                                .then(() -> {
                                    if (!user.checkPassword(requestBody.getPassword())) {
                                        return ServiceResult.failure(ResponseCode.USERNAME_OR_PASSWORD_INCORRECT);
                                    }
                                    log.info(new LoginServiceResponse(user.getId()).toString());
                                    return ServiceResult.success(new LoginServiceResponse(user.getId()));
                                }).buildResult())
                .orElse(ServiceResult.failure(ResponseCode.INTERNAL_SERVER_ERROR));
    }

    public  ServiceResult<LoginServiceResponse> verifySmsCodeLogin(SmsLoginRequestBody requestBody, String uuid) {
        String phone = requestBody.getPhoneNumber();
        String phonePrefix = PartialEncryptionHelper.getPhonePrefix(phone);
        if(!ValidateUtil.validatePhone(phone)) return ServiceResult.failure(ResponseCode.PHONE_NUMBER_EMPTY_OR_INVALID);
        return encryptedKeyService.pickEncryptionKey(1).map(
                key-> userDatumRepo.findByPhonePrefixAndPhoneHash(phonePrefix, HashUtil.calculateHmac(phone,key.getEncryptedKey())).map(
                        userDatum -> userRepo.findById(userDatum.getId()).map(
                                        user-> AuthValidator.start()
                                                .checkEmpty(requestBody.getPhoneNumber(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                                                .checkEmpty(requestBody.getSmsCode(), ResponseCode.SMS_CODE_EMPTY)
                                                .checkEmpty(requestBody.getDeviceFp(),ResponseCode.DEVICE_FINGERPRINT_REQUIRED)
                                                .check(authService.getSmsCodeService()
                                                        .verifySmsCode(phone, uuid, requestBody.getSmsCode()), ResponseCode.SMS_CODE_INCORRECT)
                                                .then(()-> ServiceResult.success(new LoginServiceResponse(user.getId())))
                                                .buildResult())
                                .orElse(ResponseCode.USER_NOT_FOUND.toServiceResult()))
                        .orElse(ServiceResult.failure(ResponseCode.INTERNAL_SERVER_ERROR)))
                .orElse(ServiceResult.failure(ResponseCode.INTERNAL_SERVER_ERROR));
    }

    public  ServiceResult<LoginServiceResponse> verifyEmailLogin(EmailLoginRequestBody requestBody, String uuid) {
        String email = requestBody.getEmail();
        String emailDisplay = PartialEncryptionHelper.getEmailDisplay(email);
        if(!ValidateUtil.validateEmail(email)) return ServiceResult.failure(ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID);
        return encryptedKeyService.pickEncryptionKey(1).map(
                key-> userDatumRepo.findByEmailDisplayAndEmailHash(emailDisplay,HashUtil.calculateHmac(email,key.getEncryptedKey())).map(
                        userDatum -> userRepo.findById(userDatum.getId()).map(
                                user-> AuthValidator.start()
                                        .checkEmpty(requestBody.getEmail(),ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID)
                                        .checkEmpty(requestBody.getEmailCode(), ResponseCode.EMAIL_CODE_EMPTY)
                                        .checkEmpty(requestBody.getDeviceFp(),ResponseCode.DEVICE_FINGERPRINT_REQUIRED)
                                        .check(authService.getEmailCodeService().verifyEmailCode(
                                                requestBody.getEmail(),uuid,requestBody.getEmailCode()), ResponseCode.EMAIL_CODE_INCORRECT)
                                        .then(()-> ServiceResult.success(new LoginServiceResponse(user.getId())))
                                        .buildResult()
                                )
                                .orElse(ResponseCode.USER_NOT_FOUND.toServiceResult()))
                        .orElseGet(ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID::toServiceResult))
                .orElse(ServiceResult.failure(ResponseCode.INTERNAL_SERVER_ERROR));
    }

    public ServiceResult<Void> logout(String refreshToken, String dfp) {
        ServiceResult<RefreshTokenPayload> res = tokenService.validateRefreshToken(refreshToken,dfp);
        if(res.isSuccess()){
            tokenService.removeAccessToken(res.getData().userId(), res.getData().deviceId());
            tokenService.removeRefreshToken(refreshToken);
            return ServiceResult.success();
        }else{
            return ServiceResult.fail(res.getResponseCode());
        }
    }
}
