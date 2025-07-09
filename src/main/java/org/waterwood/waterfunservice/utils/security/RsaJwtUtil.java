package org.waterwood.waterfunservice.utils.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.waterwood.waterfunservice.service.common.TokenResult;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Component
public class RsaJwtUtil {
    private static final String JWT_ISSUER = "waterfun";

    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final Long expiration;

    public RsaJwtUtil(PublicKey publicKey, PrivateKey privateKey, @Value("${jwt.expiration}")Long expiration) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.expiration = expiration;
    }

    public TokenResult generateToken(Map<String,Object> claims, Duration dur){
        claims.put(Claims.ISSUER,JWT_ISSUER);
        Date expDate = new Date(System.currentTimeMillis() + dur.toMillis());

        return new TokenResult(Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(expDate)
                .signWith(privateKey,Jwts.SIG.RS256)
                .compact(), dur.toSeconds());
    }

    public TokenResult generateToken(Map<String,Object> claims) {
        return generateToken(claims, Duration.ofMillis(expiration));
    }

    /**
     * Parses the JWT token and returns the claims.
     * This will validate the token signature and expiration first.
     * @param JwToken the JWT token to parse
     * @return Claims Instance
     * @throws JwtException if the token is invalid or expired
     */
    public Claims parseToken(String JwToken) throws JwtException {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(JwToken)
                .getPayload();
    }

    public boolean validateToken(String compactJws){
        try {
            Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(compactJws);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
