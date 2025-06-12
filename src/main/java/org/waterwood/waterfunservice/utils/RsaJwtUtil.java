package org.waterwood.waterfunservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
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

    public String generateToken(String subject, Duration dur){
        Map<String,Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT,subject);
        claims.put(Claims.ISSUER,JWT_ISSUER);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + dur.toMillis()))
                .signWith(privateKey,Jwts.SIG.RS256)
                .compact();
    }

    public String generateToken(String subject) {
        return generateToken(subject, Duration.ofMillis(expiration));
    }

    public Claims parseToken(String JwToken) {
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
