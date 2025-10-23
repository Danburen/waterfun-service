package org.waterwood.waterfunservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.waterwood.waterfunservice.DTO.common.ServiceResult;
import org.waterwood.waterfunservice.service.common.TokenResult;
import org.waterwood.waterfunservice.service.dto.RefreshTokenPayload;

/**
 * A service for managing AUTH tokens.
 */
public interface AuthTokenService {
    TokenResult generateAndStoreAccessToken(Long userId, String deviceId);

    TokenResult generateAndStoreRefreshToken(long userId, String deviceId, long expireInSeconds);

    TokenResult generateAndStoreRefreshToken(long userId, String deviceId);

    TokenResult RegenerateRefreshToken(String oldRefreshToken, long userId, String deviceId);

    ServiceResult<RefreshTokenPayload> validateRefreshToken(String refreshToken, String dfp);

    boolean validateAccessToken(String accessToken);

    void validateAccessToken(Claims claims);

    Claims parseToken(String ValidatedAccessToken) throws JwtException;

    void removeRefreshToken(String refreshToken);

    void removeAccessToken(Long userId, String deviceId);

    Long getCurrentUserId();
}
