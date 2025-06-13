package org.waterwood.waterfunservice.service.authServices;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.response.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponseData;
import org.waterwood.waterfunservice.DTO.common.result.AuthResult;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.request.EmailLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.PwdLoginRequestBody;
import org.waterwood.waterfunservice.DTO.request.SmsLoginRequestBody;
import org.waterwood.waterfunservice.entity.User.User;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.service.TokenService;
import org.waterwood.waterfunservice.service.common.TokenResult;
import org.waterwood.waterfunservice.utils.RsaJwtUtil;
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
    private TokenService tokenService;

    @Autowired
    UserRepository userRepo;

    public ApiResponse<LoginResponseData> loginByPassword(PwdLoginRequestBody requestBody, String captchaUUID) {
        return userRepo.findByUsername(requestBody.getUsername()).map(
                user-> validateTokenAndBuildResult(AuthValidator.start()
                        .validateUsername(requestBody.getUsername())
                        .checkEmpty(requestBody.getPassword(), ResponseCode.PASSWORD_EMPTY)
                        .checkEmpty(requestBody.getCaptcha(), ResponseCode.CAPTCHA_EMPTY)
                        .validateCode(captchaUUID, requestBody.getCaptcha(), captchaService, ResponseCode.CAPTCHA_INCORRECT)
                        .ifValidThen(() -> {
                            if (!user.checkPassword(requestBody.getPassword())) {
                                return new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT);
                            }
                            return new AuthResult(true);
                        }),requestBody.getAccessToken(), requestBody.getRefreshToken(), user))
                .orElseGet(()-> new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }

    public  ApiResponse<LoginResponseData> loginBySmsCode(SmsLoginRequestBody requestBody, String uuid) {
        return userRepo.findUserByPhone(requestBody.getUsername()).map(
                user-> validateTokenAndBuildResult(
                        AuthValidator.start()
                                .validateUsername(requestBody.getUsername())
                                .checkEmpty(requestBody.getSmsCode(), ResponseCode.SMS_CODE_EMPTY)
                                .check(smsCodeService.verifySmsCode(
                                        requestBody.getUsername(), uuid, requestBody.getSmsCode()), ResponseCode.SMS_CODE_INCORRECT),
                        requestBody.getAccessToken(), requestBody.getRefreshToken(), user))
                .orElseGet(()-> new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }

    public  ApiResponse<LoginResponseData> loginByEmail(EmailLoginRequestBody requestBody, String uuid) {
        return userRepo.findUserByEmail(requestBody.getUsername()).map(
                user-> validateTokenAndBuildResult(AuthValidator.start()
                        .validateUsername(requestBody.getUsername())
                        .checkEmpty(requestBody.getEmailCode(), ResponseCode.EMAIL_CODE_EMPTY)
                        .check(emailCodeService.verifyEmailCode(
                                requestBody.getUsername(),uuid,requestBody.getEmailCode()
                        ), ResponseCode.EMAIL_CODE_INCORRECT),
                        requestBody.getAccessToken(), requestBody.getRefreshToken(), user))
                .orElseGet(()-> new AuthResult(false, ResponseCode.USERNAME_OR_PASSWORD_INCORRECT).toLoginResponse());
    }

    /**
     * Processing Token validation and build the login response.
     * @param validator AuthValidator instance for pre-auth validation.
     * @param accessToken Access Token
     * @param refreshToken Refresh Token
     * @param user User entity
     * @return Login response instance containing the login result and tokens.
     */
    private ApiResponse<LoginResponseData> validateTokenAndBuildResult(AuthValidator validator, String accessToken, String refreshToken, User user) {
        // If the Pre-auth validation is not successful, return the error response
        AuthResult authResult = validator.buildResult();
        if(! authResult.success()){
            return authResult.toLoginResponse();
        }
        ApiResponse<LoginResponseData> res =  authResult.toLoginResponse();
        String stringUserId = String.valueOf(user.getId());
        //First time login in
        if(accessToken == null && refreshToken == null) {
            TokenResult newAccessToken = tokenService.generateAccessToken(user.getId(),user.getRole());
            String newRefreshToken = tokenService.generateAndStoreRefreshToken(user.getId());
            res.getData().setAccessToken(newAccessToken.token());
            res.getData().setRefreshToken(newRefreshToken);
            res.getData().setExpiresIn(newAccessToken.expireIn());
            return res;
        }

        if(accessToken != null){
            // validate the content of the access token
            if(tokenService.validateAccessToken(accessToken)){
                Claims claims = tokenService.parseToken(accessToken);
                if(stringUserId.equals(claims.getSubject()) && user.getRole().name().toLowerCase().equals(claims.get("role"))){
                    res.getData().setExpiresIn((claims.getExpiration().getTime() - System.currentTimeMillis())/1000);
                    res.getData().setUsername(user.getUsername());
                    res.getData().setUserId(user.getId());
                } else {
                    // Access token is invalid, return error response
                    return new AuthResult(false, ResponseCode.ACCESS_TOKEN_INVALID).toLoginResponse();
                }
            }
        }
        return res;
    }
}
