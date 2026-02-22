package com.crm.auth.service;

import com.crm.auth.dto.JwtPayload;
import com.crm.sharedlib.core.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
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
    public static final String DEVICE_INFO_KEY = "deviceInfo";

    @Value("${app.token.signing.key}")
    private String jwtSigningKey;
    @Value("${app.token.expiration}")
    private Long EXPIRATION_TIME;

    public String generateToken(Long userId, String login, String deviceInfo) {
        return Jwts.builder()
                .claim(USER_ID_KEY, userId.toString())
                .claim(USER_LOGIN_KEY, login)
                .claim(DEVICE_INFO_KEY, deviceInfo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION_TIME)))
                .signWith(getSingingKey())
                .compact();
    }

    public JwtPayload getPayloadFromJwtToken(String token) {
        Claims claims = getClaims(token);

        if (isExpired(claims)) {
            throw new UnauthorizedException("Unauthorized");
        }

        Long userId = Long.valueOf((String) claims.get(USER_ID_KEY));
        String userLogin = (String) claims.get(USER_LOGIN_KEY);
        String deviceInfo = (String) claims.get(DEVICE_INFO_KEY);

        return new JwtPayload(userId, userLogin, deviceInfo);
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

    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private SecretKey getSingingKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
