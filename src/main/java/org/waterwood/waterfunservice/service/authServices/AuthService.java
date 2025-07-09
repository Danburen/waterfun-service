package org.waterwood.waterfunservice.service.authServices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.service.UserManagerService;
import org.waterwood.waterfunservice.service.dto.LoginServiceResponse;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.entity.user.User;
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
    @Autowired
    private UserManagerService userManagerService;

    /**
     * Processing Token validation and build the login response.
     * @param validator AuthValidator instance for pre-auth validation.
     * @param accessToken Access Token
     * @param refreshToken Refresh Token
     * @param user User entity
     * @return Login response instance containing the login result and tokens.
     */
    public ApiResponse<LoginServiceResponse> validateTokenAndBuildResult(AuthValidator validator, String accessToken, String refreshToken, User user) {
        // If the Pre-auth validation is not successful, return the error response
        ApiResponse<LoginServiceResponse> result = validator.buildResult();
        if(! result.isSuccess()){
            return result;
        }
        return validateTokens(accessToken, refreshToken, user);
    }

    public ApiResponse<LoginServiceResponse> validateTokens(String accessToken, String refreshToken, User user) {
        //First time login in
        if(accessToken == null && refreshToken == null) {
            long userId = user.getId();
            TokenResult newAccessToken = tokenService.generateAccessToken(
                    userId,
                    userManagerService.getUserRoles(userId),
                    userManagerService.getUserPermissions(userId));
            String newRefreshToken = tokenService.generateAndStoreRefreshToken(user.getId());
            return ApiResponse.success(LoginServiceResponse.builder()
                    .accessToken(newAccessToken.token())
                    .refreshToken(newRefreshToken)
                    .expiresIn(newAccessToken.expireIn()).build());
        }

        if(accessToken != null){
            // validate the content of the access token
            try{
                Claims claims = tokenService.parseToken(accessToken);
                return ApiResponse.success(LoginServiceResponse.builder()
                        .expiresIn((claims.getExpiration().getTime() - System.currentTimeMillis())/1000)
                        .username(user.getUsername())
                        .userId(user.getId()).build());
            }catch (Exception e){
                if(e instanceof ExpiredJwtException) {
                    return ResponseCode.ACCESS_TOKEN_EXPIRED.toApiResponse();
                } else {
                    return ResponseCode.INTERNAL_SERVER_ERROR.toApiResponse();
                }
            }
        }
        return ResponseCode.ACCESS_TOKEN_EXPIRED.toApiResponse();
    }

    public boolean isTokenValid(String accessToken, User user) {
        if (!tokenService.validateAccessToken(accessToken)) return false;
        Claims claims = tokenService.parseToken(accessToken);
        return String.valueOf(user.getId()).equals(claims.getSubject())
                && userManagerService.getUserRoles(user.getId()).stream()
                .map(role-> role.getName().toLowerCase())
                .toList().equals(claims.get("roles"))
                && userManagerService.getUserPermissions(user.getId()).stream()
                .map(role-> role.getName().toLowerCase())
                .toList().equals(claims.get("perms"));
    }
}
