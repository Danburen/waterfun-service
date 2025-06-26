package org.waterwood.waterfunservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.service.common.TokenResult;
import org.waterwood.waterfunservice.utils.RsaJwtUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public TokenResult generateAccessToken(Long userId, List<Role> roles, List<Permission> extraPerms) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, userId);
        claims.put("roles",roles.stream().map(role-> role.getName().toLowerCase()).toList());
        claims.put("perms",extraPerms);
        return rsaJwtUtil.generateToken(claims);
    }

    public String generateAndStoreRefreshToken(Long userId) {
        String refreshToken = generateNewUUID();
        saveValue(buildRawRedisKey("ref",refreshToken), userId.toString(), Duration.ofSeconds(accessTokenExpire));
        return refreshToken;
    }

    /**
     * Validates the refresh token and returns the userId if valid.
     * <p><b>Refresh Token will be removed </b>after validate</p>
     * @param refreshToken the refresh token to validate
     * @return Long of <b>UserID</b> if the token is valid, null otherwise
     */
    public @Nullable Long validateRefreshToken(String refreshToken) {
        String key = buildRawRedisKey("ref", refreshToken);
        String userId = getValue(key);
        if (userId == null) {
            return null;
        }
        Long expireTime = getExpire(key);
        if (expireTime == null || expireTime <= 0) {
            return null;
        }
//        removeValue(key);
        return Long.valueOf(userId);
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            return rsaJwtUtil.validateToken(accessToken); // Token is invalid
        } catch (Exception e) {
            return false; // Token is invalid or expired
        }
    }

    public TokenResult refreshAccessToken(String refreshToken,List<Role> roles, List<Permission> extraPerms) {
        Long userId = validateRefreshToken(refreshToken);
        if(userId == null) {
            return null; // Refresh token is invalid
        }
        return generateAccessToken(userId,roles,extraPerms);
    }

    public Claims parseToken(String ValidatedAccessToken) throws JwtException{
        return rsaJwtUtil.parseToken(ValidatedAccessToken);
    }

    public void removeRefreshToken(String refreshToken) {
        removeValue(buildRawRedisKey("ref", refreshToken));
    }
}
