package org.waterwood.waterfunservice.service.Impl;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ServiceResult;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.DeviceService;
import org.waterwood.waterfunservice.service.AuthTokenService;
import org.waterwood.waterfunservice.service.common.TokenResult;
import org.waterwood.waterfunservice.service.dto.RefreshTokenPayload;
import org.waterwood.waterfunservice.utils.security.RsaJwtUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RSAJwtTokenService implements AuthTokenService {
    private final RsaJwtUtil rsaJwtUtil;
    private final RedisHelper<String> redisHelper;

    private static final String REDIS_TOKEN_KEY_PREFIX = "token";
    private static final String REFRESH_TOKEN_KEY = "ref";
    private static final String ACCESS_TOKEN_KEY = "jti";

    private final Gson gson = new Gson();
    private final DeviceService deviceService;


    @Value("${token.refresh.expiration:604800}") // Default to 7 days in seconds
    private Long refreshTokenExpire;
    @Value("${token.access.expiration:3600}") // Default to 1 hour in seconds
    private Long accessTokenExpire;
    public RSAJwtTokenService(RedisHelper<String> redisHelper, RsaJwtUtil rsaJwtUtil, DeviceService deviceService) {
        this.redisHelper = redisHelper;
        this.rsaJwtUtil = rsaJwtUtil;
        redisHelper.setKeyPrefix(REDIS_TOKEN_KEY_PREFIX);
        this.deviceService = deviceService;
    }

    @Override
    public TokenResult generateAndStoreAccessToken(Long userId, String deviceId) {
        String jti = redisHelper.generateNewUUID();
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT,String.valueOf(userId));
        claims.put(Claims.ID,jti);
        claims.put("did", deviceId);

        Duration expire = Duration.ofSeconds(accessTokenExpire);
        TokenResult result = rsaJwtUtil.generateToken(claims,expire);
        redisHelper.saveValue(redisHelper.buildKeys(ACCESS_TOKEN_KEY, userId.toString(),deviceId),jti,expire);
        return result;
    }

    /**
     * Generate and store refresh token
     * @param userId the user ID
     * @param deviceId the identify of device
     * @param expireInSeconds expiration in seconds
     * @return Token result
     */
    @Override
    public TokenResult generateAndStoreRefreshToken(long userId, String deviceId, long expireInSeconds) {
        String refreshToken = redisHelper.generateNewUUID();
        redisHelper.saveValue(redisHelper.buildKeys(REFRESH_TOKEN_KEY,refreshToken),
                gson.toJson(Map.of("userId",userId,
                        "did",deviceId)),
                Duration.ofSeconds(expireInSeconds));
        log.info("Refresh token: {}", refreshToken);
        return new TokenResult(refreshToken,expireInSeconds);
    }

    @Override
    public TokenResult generateAndStoreRefreshToken(long userId, String deviceId) {
        return generateAndStoreRefreshToken(userId,deviceId,refreshTokenExpire);
    }

    @Override
    public TokenResult RegenerateRefreshToken(String oldRefreshToken, long userId, String deviceId) {
        long restExpire = redisHelper.getExpire(redisHelper.buildKeys(REFRESH_TOKEN_KEY,oldRefreshToken));
        return generateAndStoreRefreshToken(userId,deviceId,restExpire);
    }

    /**
     * Validates the refresh tokenValue and returns the userId if valid.
     * <p><b>Refresh Token will be removed </b>after validate</p>
     * @param refreshToken the refresh tokenValue to validate
     * @return Long of <b>UserID</b> if the tokenValue is valid
     */
    @Override
    public ServiceResult<RefreshTokenPayload> validateRefreshToken(String refreshToken, String dfp) {
        String key = redisHelper.buildKeys(REFRESH_TOKEN_KEY,refreshToken);
        String jsonRes = redisHelper.getValue(key);
        if (jsonRes == null) {
            return ServiceResult.failure(ResponseCode.REFRESH_TOKEN_INVALID);
        }
        Long expireTime = redisHelper.getExpire(key);
        if (expireTime == null || expireTime <= 0) {
            return ServiceResult.failure(ResponseCode.REFRESH_TOKEN_EXPIRED);
        }
        long userId = Double.valueOf((double)gson.fromJson(jsonRes, Map.class).get("userId")).longValue();
        String originalDid = (String) gson.fromJson(jsonRes, Map.class).get("did");
        String did = deviceService.generateDeviceId(userId,dfp);
        if(! did.equals(originalDid)) { // Device Fingerprint changed
            log.info("User ID: {} , device Fingerprint changed: {} -> {}",userId,originalDid,dfp);
        }
        return ServiceResult.success(new RefreshTokenPayload(userId,dfp));
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            return rsaJwtUtil.validateToken(accessToken); // Token is invalid
        } catch (Exception e) {
            return false; // Token is invalid or expired
        }
    }

    @Override
    public void validateAccessToken(Claims claims) {
        String userId = claims.getSubject();
        String iss = claims.getIssuer();
        String jti = claims.getId();
        String did = (String) claims.get("did");
        if(iss == null || !iss.equals(rsaJwtUtil.getIssuer())) throw new JwtException("Invalid issuer");
        String jtiKey = redisHelper.buildKeys(redisHelper.buildKeys(ACCESS_TOKEN_KEY, userId,did));
        String originalJti = redisHelper.getValue(jtiKey);
        if(originalJti == null || !originalJti.equals(jti)){
            throw new JwtException("Invalid token ID");
        }
    }

    @Override
    public Claims parseToken(String ValidatedAccessToken) throws JwtException{
        return rsaJwtUtil.parseToken(ValidatedAccessToken);
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        redisHelper.removeValue(redisHelper.buildKeys(REFRESH_TOKEN_KEY, refreshToken));
    }

    @Override
    public void removeAccessToken(Long userId, String deviceId) {
        redisHelper.removeValue(redisHelper.buildKeys(ACCESS_TOKEN_KEY, userId.toString(),deviceId));
    }

    @Override
    public Long getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return Long.parseLong(jwt.getSubject());
    }
}
