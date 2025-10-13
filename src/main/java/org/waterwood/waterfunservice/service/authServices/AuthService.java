package org.waterwood.waterfunservice.service.authServices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.repository.UserProfileRepo;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.service.DeviceService;
import org.waterwood.waterfunservice.service.UserManagerService;
import org.waterwood.waterfunservice.service.common.TokenPair;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.Impl.RSAJwtTokenService;
import org.waterwood.waterfunservice.service.common.TokenResult;
import org.waterwood.waterfunservice.service.dto.RefreshTokenPayload;

@Service
@Getter
@Slf4j
public class AuthService {
    private final CaptchaService captchaService;
    private final SmsCodeService smsCodeService;
    private final EmailCodeService emailCodeService;
    private final RSAJwtTokenService tokenService;
    private final UserManagerService userManagerService;
    private final UserProfileRepo userProfileRepo;
    private final DeviceService deviceService;
    private final UserRepository userRepository;

    public AuthService(CaptchaService cs, SmsCodeService smcs, EmailCodeService emc, RSAJwtTokenService ts, UserManagerService us, UserProfileRepo up, DeviceService ds, UserRepository ur) {
        this.captchaService = cs;
        this.smsCodeService = smcs;
        this.emailCodeService = emc;
        this.tokenService = ts;
        this.userManagerService = us;
        this.userProfileRepo = up;
        this.deviceService = ds;
        this.userRepository = ur;
    }

    public TokenPair createNewTokens(long userId,String deviceFingerprint) {
        String deviceId = deviceService.generateAndStoreDeviceId(userId,deviceFingerprint);
        TokenResult accessToken = tokenService.generateAndStoreAccessToken(userId,deviceId);
        TokenResult refreshToken = tokenService.generateAndStoreRefreshToken(userId,deviceId);
        return new TokenPair(
                accessToken.tokenValue(), accessToken.expire(),
                refreshToken.tokenValue(), refreshToken.expire());
    }

    @Deprecated
    public ApiResponse<Long> validateAccessToken(String accessToken) {
        if(accessToken != null && ! accessToken.isEmpty()) {
            // validate the content of the access tokenValue
            try{
                Claims claims = tokenService.parseToken(accessToken);
                tokenService.validateAccessToken(claims);
                Long userId = Long.parseLong(claims.getSubject());
                return ApiResponse.success(userId);
            }catch (Exception e){
                if(e instanceof ExpiredJwtException) {
                    return ResponseCode.ACCESS_TOKEN_EXPIRED.toApiResponse();
                } else if(e instanceof InvalidClaimException) {
                    return ResponseCode.ACCESS_TOKEN_INVALID.toApiResponse();
                }else{
                    log.info("An internal server error occurred {}",e.getMessage());
                    return ResponseCode.INTERNAL_SERVER_ERROR.toApiResponse();
                }
            }
        }
        return ResponseCode.ACCESS_TOKEN_EXPIRED.toApiResponse();
    }

    /**
     * Return the api response of refresh access tokenValue operation.
     * <p>for future extension or refactor , we temporarily use api response instead of OpResult</p>
     * @param refreshToken refresh tokenValue
     * @return ApiResponse type Token result that contains tokenValue and expirations.
     *
     */
    public ApiResponse<TokenResult> refreshAccessToken(String refreshToken,String dfp) {
        if(dfp == null || dfp.isEmpty()) return ResponseCode.DEVICE_FINGERPRINT_REQUIRED.toApiResponse();
        if(refreshToken == null || refreshToken.isEmpty()) return ResponseCode.REFRESH_TOKEN_MISSING.toApiResponse();
        ApiResponse<RefreshTokenPayload> validRes = tokenService.validateRefreshToken(refreshToken,dfp);
        if(validRes.isSuccess()){
            RefreshTokenPayload payload = validRes.getData();
            long userId = payload.userId();
            String deviceId = payload.deviceId();
            return userRepository.findById(userId).map(user->
                            ApiResponse.success(tokenService.RegenerateRefreshToken(refreshToken,userId,deviceId)))
                    .orElse(ApiResponse.failure(ResponseCode.USER_NOT_FOUND));
        }else{
            return ApiResponse.failure(validRes.getResponseCode());
        }
    }

}
