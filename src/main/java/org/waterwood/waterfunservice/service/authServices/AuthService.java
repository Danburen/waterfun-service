package org.waterwood.waterfunservice.service.authServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponse;
import org.waterwood.waterfunservice.DTO.common.result.AuthResult;
import org.waterwood.waterfunservice.DTO.common.ErrorCode;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.entity.User.User;
import org.waterwood.waterfunservice.repository.UserDatumRepository;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.utils.validator.AuthValidator;

@Service
public class AuthService {
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private SmsCodeService smsCodeService;
    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    UserRepository userRepo;

    public LoginResponse loginByPassword(PwdLoginRequestBody requestBody, String captchaUUID) {
        return userRepo.findByUsername(requestBody.getUsername()).map(
                user-> {
                    AuthResult authResult = AuthValidator.start()
                            .validateUsername(requestBody.getUsername())
                            .checkEmpty(requestBody.getPassword(), ErrorCode.PASSWORD_EMPTY)
                            .checkEmpty(requestBody.getCaptcha(), ErrorCode.CAPTCHA_EMPTY)
                            .validateCode(captchaUUID, requestBody.getCaptcha(), captchaService, ErrorCode.CAPTCHA_INCORRECT)
                            .ifValidThen(() -> {
                                if (!user.checkPassword(requestBody.getPassword())) {
                                    return new AuthResult(false, ErrorCode.USERNAME_OR_PASSWORD_INCORRECT);
                                }
                                return new AuthResult(true);
                            })
                            .buildResult();

                    return authResult.toLoginResponse(LoginResponse
                            .builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .build());
                }).orElseGet(()-> new AuthResult(false, ErrorCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }

    public LoginResponse loginBySmsCode(SmsLoginRequestBody requestBody,String uuid) {
        return userRepo.findUserByPhone(requestBody.getUsername()).map(
                user->{
                    AuthResult authResult = AuthValidator.start()
                            .validateUsername(requestBody.getUsername())
                            .checkEmpty(requestBody.getSmsCode(), ErrorCode.SMS_CODE_EMPTY)
                            .check(smsCodeService.verifySmsCode(
                                    requestBody.getUsername(),
                                    uuid,
                                    requestBody.getSmsCode()),ErrorCode.SMS_CODE_INCORRECT)
                            .buildResult();
                    return authResult.toLoginResponse(LoginResponse
                            .builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .build());
                }).orElseGet(()-> new AuthResult(false, ErrorCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }

    public LoginResponse loginByEmail(EmailLoginRequestBody requestBody, String uuid) {
        return userRepo.findUserByEmail(requestBody.getUsername()).map(
                user->{
                    AuthResult authResult = AuthValidator.start()
                            .validateUsername(requestBody.getUsername())
                            .checkEmpty(requestBody.getEmailCode(), ErrorCode.EMAIL_CODE_EMPTY)
                            .check(emailCodeService.verifyEmailCode(
                                    requestBody.getUsername(),uuid,requestBody.getEmailCode()
                            ), ErrorCode.EMAIL_CODE_INCORRECT)
                            .buildResult();
                    return authResult.toLoginResponse(LoginResponse
                            .builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .build());
                }).orElseGet(()-> new AuthResult(false, ErrorCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }


}
