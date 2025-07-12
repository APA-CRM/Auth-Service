package com.crm.auth.service;

import com.crm.sharedlib.dto.response.AuthResponse;
import com.crm.sharedlib.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    public static final String USER_ID_KEY = "userId";

    public static final String USER_LOGIN_KEY = "login";

    @Value("${app.token.signing.key}")
    private String jwtSigningKey;
    @Value("${app.token.expiration}")
    private Long EXPIRATION_TIME;

    public String generateToken(Long userId, String login) {
        return Jwts.builder()
                .claim(USER_ID_KEY, userId)
                .claim(USER_LOGIN_KEY, login)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION_TIME)))
                .signWith(getSingingKey())
                .compact();
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSingingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public AuthResponse getPayloadFromJwtToken(String token) {
        Claims claims = getClaims(token);

        Integer userId = (int) claims.get(USER_ID_KEY);
        String userLogin = (String) claims.get(USER_LOGIN_KEY);

        return new AuthResponse(userId, userLogin);
    }

    public boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private SecretKey getSingingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
