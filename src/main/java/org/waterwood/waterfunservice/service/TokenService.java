package org.waterwood.waterfunservice.service;

import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.utils.RsaJwtUtil;

import java.time.Duration;

@Service
public class TokenService extends RedisServiceBase<String> {
    private final RsaJwtUtil rsaJwtUtil;
    private static final String redisKeyPrefix = "token";

    @Value("${jwt.refresh-token.expire:604800}") // Default to 7 days in seconds
    private Long refreshTokenExpire;
    @Value("${jwt.access-token.expire:3600}") // Default to 1 hour in seconds
    private Long accessTokenExpire;
    public TokenService(RsaJwtUtil rsaJwtUtil,RedisRepository<String> redisRepository) {
        super(redisKeyPrefix,redisRepository);
        this.rsaJwtUtil = rsaJwtUtil;
    }

    public String generateAccessToken(String userId) {
        return rsaJwtUtil.generateToken(userId);
    }

    public String generateAndStoreRefreshToken(String userId) {
        String refreshToken = generateNewUUID();
        saveValue(buildRawRedisKey("ref",refreshToken), userId, Duration.ofSeconds(accessTokenExpire));
        return refreshToken;
    }

    /**
     * Validates the refresh token and returns the userId if valid.
     * <p><b>Refresh Token will be removed </b>after validate</p>
     * @param refreshToken the refresh token to validate
     * @return String of <b>UserID</b> if the token is valid, null otherwise
     */
    public @Nullable String validateRefreshToken(String refreshToken) {
        String key = buildRawRedisKey("ref", refreshToken);
        String userId = getValue(key);
        if (userId == null) {
            return null;
        }
        Long expireTime = getExpire(key);
        if (expireTime == null || expireTime <= 0) {
            return null;
        }
        // Remove the token after validation
        removeValue(key);
        return userId;
    }

    public @Nullable String validateAccessToken(String accessToken) {
        try {
            if(!rsaJwtUtil.validateToken(accessToken)) {
                return null; // Token is invalid
            }
            Claims claims = rsaJwtUtil.parseToken(accessToken);
            return claims.getSubject(); // No userId in the token
        } catch (Exception e) {
            return null; // Token is invalid or expired
        }
    }

    public @Nullable String refreshAccessToken(String userId) {
        userId = validateRefreshToken(userId);
        if(userId == null) {
            return null; // Refresh token is invalid
        }
        return generateAccessToken(userId);
    }
}
