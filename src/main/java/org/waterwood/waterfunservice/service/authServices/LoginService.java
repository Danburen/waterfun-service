package org.waterwood.waterfunservice.service.authServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.response.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponseData;
import org.waterwood.waterfunservice.DTO.common.result.AuthResult;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.utils.streamApi.AuthValidator;

@Service
public class LoginService {
    @Autowired
    UserRepository userRepo;
    @Autowired
    AuthService authService;

    public ApiResponse<LoginResponseData> loginByPassword(PwdLoginRequestBody requestBody, String captchaUUID) {
        return userRepo.findByUsername(requestBody.getUsername()).map(
                        user-> authService.validateTokenAndBuildResult(AuthValidator.start()
                                .checkEmpty(requestBody.getUsername(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                                .checkEmpty(requestBody.getPassword(), ResponseCode.PASSWORD_EMPTY)
                                .checkEmpty(requestBody.getCaptcha(), ResponseCode.CAPTCHA_EMPTY)
                                .validateCode(captchaUUID, requestBody.getCaptcha(),authService.getCaptchaService(), ResponseCode.CAPTCHA_INCORRECT)
                                .ifValidThen(() -> {
                                    if (!user.checkPassword(requestBody.getPassword())) {
                                        return new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT);
                                    }
                                    return new AuthResult(true);
                                }),requestBody.getAccessToken(), requestBody.getRefreshToken(), user))
                .orElseGet(()-> new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }

    public  ApiResponse<LoginResponseData> loginBySmsCode(SmsLoginRequestBody requestBody, String uuid) {
        return userRepo.findUserByPhone(requestBody.getPhoneNumber()).map(
                        user-> authService.validateTokenAndBuildResult(AuthValidator.start()
                                        .checkEmpty(requestBody.getPhoneNumber(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                                        .checkEmpty(requestBody.getSmsCode(), ResponseCode.SMS_CODE_EMPTY)
                                        .check(authService.getSmsCodeService().verifySmsCode(
                                                requestBody.getPhoneNumber(), uuid, requestBody.getSmsCode()), ResponseCode.SMS_CODE_INCORRECT),
                                requestBody.getAccessToken(), requestBody.getRefreshToken(), user))
                .orElseGet(()-> new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }

    public  ApiResponse<LoginResponseData> loginByEmail(EmailLoginRequestBody requestBody, String uuid) {
        return userRepo.findUserByEmail(requestBody.getEmail()).map(
                        user-> authService.validateTokenAndBuildResult(AuthValidator.start()
                                        .checkEmpty(requestBody.getEmail(),ResponseCode.USERNAME_EMPTY_OR_INVALID)
                                        .checkEmpty(requestBody.getEmailCode(), ResponseCode.EMAIL_CODE_EMPTY)
                                        .check(authService.getEmailCodeService().verifyEmailCode(
                                                requestBody.getEmail(),uuid,requestBody.getEmailCode()
                                        ), ResponseCode.EMAIL_CODE_INCORRECT),
                                requestBody.getAccessToken(), requestBody.getRefreshToken(), user))
                .orElseGet(()-> new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }
}
