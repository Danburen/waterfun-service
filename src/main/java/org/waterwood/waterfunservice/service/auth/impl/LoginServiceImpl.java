package org.waterwood.waterfunservice.service.auth.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.service.auth.LoginService;
import org.waterwood.waterfunservice.infrastructure.security.EncryptionDataKey;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.entity.user.UserDatum;
import org.waterwood.waterfunservice.infrastructure.exception.AuthException;
import org.waterwood.waterfunservice.infrastructure.exception.BusinessException;
import org.waterwood.waterfunservice.infrastructure.exception.ServiceException;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserDatumRepo;
import org.waterwood.waterfunservice.infrastructure.security.EncryptedKeyService;
import org.waterwood.waterfunservice.dto.request.auth.EmailLoginRequestBody;
import org.waterwood.waterfunservice.dto.request.auth.PwdLoginRequestBody;
import org.waterwood.waterfunservice.dto.request.auth.SmsLoginRequestBody;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservice.service.dto.RefreshTokenPayload;
import org.waterwood.waterfunservice.infrastructure.utils.codec.HashUtil;

import java.util.Optional;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepo;
    private final AuthServiceImpl authService;
    private final RSAJwtTokenService tokenService;
    private final UserDatumRepo userDatumRepo;
    private final EncryptedKeyService encryptedKeyService;
    private final CaptchaServiceImpl captchaService;
    private final EmailCodeService emailCodeService;
    private final SmsCodeService smsCodeService;

    public LoginServiceImpl(UserRepository ur, AuthServiceImpl as, RSAJwtTokenService ts, CaptchaServiceImpl cs, EncryptedKeyService edks, UserDatumRepo udr, EmailCodeService emailCodeService, SmsCodeService smsCodeService) {
        this.userRepo = ur;
        this.authService = as;
        this.tokenService = ts;
        this.captchaService = cs;
        this.encryptedKeyService = edks;
        this.userDatumRepo = udr;
        this.emailCodeService = emailCodeService;
        this.smsCodeService = smsCodeService;
    }



    @Override
    public User login(PwdLoginRequestBody body, String verifyUUIDKey){
        Optional<User> user = userRepo.findByUsername(body.getUsername());
        return user.map(u->{
            if(!u.checkPassword(body.getPassword())){
                throw new AuthException(ResponseCode.USERNAME_OR_PASSWORD_INCORRECT);
            }
            if(! captchaService.validateCaptcha(verifyUUIDKey,body.getCaptcha())){
                throw new AuthException(ResponseCode.CAPTCHA_INCORRECT);
            }
            return u;
        }).orElseThrow(()-> new ServiceException("User not found"));
    }

    @Override
    public User login(SmsLoginRequestBody body, String verifyUUIDKey){
        String phone = body.getPhoneNumber();
        EncryptionDataKey key= encryptedKeyService.pickEncryptionKey(1)
                .orElseThrow(() -> new ServiceException("Couldn't pick encryption key"));
        UserDatum datum = userDatumRepo.findByPhoneHash(HashUtil.Sha256HmacString(phone,key.getEncryptedKey()))
                .orElseThrow(() ->  new BusinessException(ResponseCode.PASSWORD_EMPTY_OR_INVALID));
        boolean verified = smsCodeService.verifySmsCode(phone,verifyUUIDKey,body.getSmsCode());
        if(verified){
            return datum.getUser();
        }else{
            throw new BusinessException(ResponseCode.SMS_CODE_INCORRECT);
        }
    }

    @Override
    public User login(EmailLoginRequestBody body, String verifyUUIDKey){
        String email = body.getEmail();
        EncryptionDataKey key= encryptedKeyService.pickEncryptionKey(1)
                .orElseThrow(() -> new ServiceException("Couldn't pick encryption key"));
        UserDatum userDatum = userDatumRepo.findByEmailHash(HashUtil.Sha256HmacString(email,key.getEncryptedKey()))
                .orElseThrow(() ->  new BusinessException(ResponseCode.USERNAME_OR_PASSWORD_INCORRECT));
        boolean verified = emailCodeService.verifyEmailCode(email,verifyUUIDKey,body.getEmailCode());
        if(verified){
            return userDatum.getUser();
        }else{
            throw new BusinessException(ResponseCode.EMAIL_CODE_INCORRECT);
        }
    }
    /*
    @Deprecated
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

    @Deprecated
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
    }*/
    @Override
    public boolean logout(String refreshToken, String dfp) {
        RefreshTokenPayload payload = tokenService.validateRefreshToken(refreshToken,dfp);
            tokenService.removeAccessToken(payload.userId(), payload.deviceId());
            tokenService.removeRefreshToken(refreshToken);
        return true;
    }
}
