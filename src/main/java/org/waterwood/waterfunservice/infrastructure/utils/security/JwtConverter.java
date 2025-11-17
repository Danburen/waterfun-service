package org.waterwood.waterfunservice.infrastructure.utils.security;

import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.waterwood.waterfunservice.dto.response.context.LoginUser;
import org.waterwood.waterfunservice.service.auth.impl.RSAJwtTokenService;
import org.waterwood.waterfunservice.infrastructure.security.RsaJwtUtil;
import org.waterwood.waterfunservice.infrastructure.utils.context.ThreadLocalUtil;

@Component
public class JwtConverter implements Converter<String, Jwt> {
    private final RsaJwtUtil rsaJwtUtil;
    private final RSAJwtTokenService tokenService;

    public JwtConverter(RsaJwtUtil rsaJwtUtil, RSAJwtTokenService tokenService) {
        this.rsaJwtUtil = rsaJwtUtil;
        this.tokenService = tokenService;
    }

    @Override
    public Jwt convert(@NotNull String token) {
            Claims claims = rsaJwtUtil.parseToken(token);
            tokenService.validateAccessTokenAndRejectOld(claims);

            long userId = Long.parseLong(claims.getSubject());
            String jti = claims.getId();
            LoginUser loginUser = new LoginUser(userId, jti);
            ThreadLocalUtil.set(loginUser);
            return Jwt.withTokenValue(token)
                    .header("alg","RS256")
                    .header("typ","JWT")
                    .claims(c -> c.putAll(claims))
                    .issuedAt(claims.getIssuedAt().toInstant())
                    .expiresAt(claims.getExpiration().toInstant())
                    .build();
    }
}
