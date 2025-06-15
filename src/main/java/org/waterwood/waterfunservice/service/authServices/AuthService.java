package org.waterwood.waterfunservice.service.authServices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Getter;
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
import org.waterwood.waterfunservice.utils.streamApi.AuthValidator;

@Service
@Getter
public class AuthService {
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private SmsCodeService smsCodeService;
    @Autowired
    private EmailCodeService emailCodeService;
    @Autowired
    private TokenService tokenService;

    /**
     * Processing Token validation and build the login response.
     * @param validator AuthValidator instance for pre-auth validation.
     * @param accessToken Access Token
     * @param refreshToken Refresh Token
     * @param user User entity
     * @return Login response instance containing the login result and tokens.
     */
    public ApiResponse<LoginResponseData> validateTokenAndBuildResult(AuthValidator validator, String accessToken, String refreshToken, User user) {
        // If the Pre-auth validation is not successful, return the error response
        AuthResult authResult = validator.buildResult();
        if(! authResult.success()){
            return authResult.toLoginResponse();
        }
        ApiResponse<LoginResponseData> res =  authResult.toLoginResponse();
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
            try{
                Claims claims = tokenService.parseToken(accessToken);
                res.getData().setExpiresIn((claims.getExpiration().getTime() - System.currentTimeMillis())/1000);
                res.getData().setUsername(user.getUsername());
                res.getData().setUserId(user.getId());
            }catch (Exception e){
                if(e instanceof ExpiredJwtException) {
                    return new AuthResult(false, ResponseCode.ACCESS_TOKEN_EXPIRED).toLoginResponse();
                } else {
                    return new AuthResult(false, ResponseCode.ACCESS_TOKEN_INVALID).toLoginResponse();
                }
            }
        }
        return res;
    }

    public boolean isTokenValid(String accessToken, User user) {
        if (!tokenService.validateAccessToken(accessToken)) return false;
        Claims claims = tokenService.parseToken(accessToken);
        return String.valueOf(user.getId()).equals(claims.getSubject())
                && user.getRole().name().toLowerCase().equals(claims.get("role"));
    }
}
