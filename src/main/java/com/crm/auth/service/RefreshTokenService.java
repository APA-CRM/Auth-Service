package com.crm.auth.service;

import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.persistance.repository.RefreshTokenRepository;
import com.crm.auth.utils.SecureStringGenerator;
import com.crm.sharedlib.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.refresh-token.expiration}")
    private Integer refreshTokenExpirationInMinutes;

    @Value("${app.refresh-token.length}")
    private Integer refreshTokenLength;

    @Transactional
    public RefreshToken getRefreshTokenBySignInRequest(User user, String deviceInfo) {
        RefreshToken refreshToken = getRefreshTokenByDeviceInfoAndUserId(deviceInfo, user);

        updateRefreshTokenEntity(refreshToken, deviceInfo);
        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken createRefreshToken(String deviceInfo, User user) {
        RefreshToken refreshToken = new RefreshToken();

        updateRefreshTokenEntity(refreshToken, deviceInfo);

        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken validateAndRecreateRefreshToken(String refreshTokenString, String deviceInfo) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new UnauthorizedException("Refresh token is not valid"));

        checkIfTokenExpired(refreshToken);

        updateRefreshTokenEntity(refreshToken, deviceInfo);

        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken getRefreshTokenByDeviceInfoAndUserId(String deviceInfo, User user) {
        return refreshTokenRepository.findByDeviceInfoAndUser(deviceInfo, user)
                .orElseGet(RefreshToken::new);
    }

    private void updateRefreshTokenEntity(RefreshToken refreshToken, String deviceInfo) {
        String token = SecureStringGenerator.generateSecureString(refreshTokenLength);

        refreshToken.setToken(token);
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setExpiredAt(Instant.now().plus(refreshTokenExpirationInMinutes, ChronoUnit.MINUTES));
    }

    private void checkIfTokenExpired(RefreshToken refreshToken) {
        if (nonNull(refreshToken) &&
                refreshToken.getExpiredAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token is expired");
        }
    }

}