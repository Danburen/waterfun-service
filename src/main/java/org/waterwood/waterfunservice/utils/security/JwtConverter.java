package org.waterwood.waterfunservice.utils.security;

import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.waterwood.waterfunservice.service.TokenService;

@Component
public class JwtConverter implements Converter<String, Jwt> {
    private final RsaJwtUtil rsaJwtUtil;
    private final TokenService tokenService;

    public JwtConverter(RsaJwtUtil rsaJwtUtil, TokenService tokenService) {
        this.rsaJwtUtil = rsaJwtUtil;
        this.tokenService = tokenService;
    }

    @Override
    public Jwt convert(@NotNull String token) {
        Claims claims = rsaJwtUtil.parseToken(token);
        tokenService.validateAccessToken(claims);
        return Jwt.withTokenValue(token)
                .header("alg","RS256")
                .header("typ","JWT")
                .claims(c -> c.putAll(claims))
                .issuedAt(claims.getIssuedAt().toInstant())
                .expiresAt(claims.getExpiration().toInstant())
                .build();
    }
}
