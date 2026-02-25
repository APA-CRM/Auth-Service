package com.crm.auth.service;

import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.persistance.repository.RefreshTokenRepository;
import com.crm.auth.utils.SecureStringGenerator;
import com.crm.sharedlib.core.exception.ForbiddenException;
import com.crm.sharedlib.core.exception.NotFoundException;
import com.crm.sharedlib.core.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public RefreshToken createRefreshToken(String deviceInfo, User user) {
        RefreshToken refreshToken = new RefreshToken();

        updateRefreshTokenEntity(refreshToken, deviceInfo);

        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getRefreshTokenOrThrowException(UUID id) {
        return refreshTokenRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Refresh token is not found"));
    }

    public RefreshToken getRefreshTokenOrThrowUnauthorizedException(String refreshTokenString) {
        return refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
    }

    public List<RefreshToken> getRefreshTokensByUser(User user) {
        return refreshTokenRepository.findByUser(user);
    }

    @Transactional
    public RefreshToken updateRefreshToken(RefreshToken refreshToken, String deviceInfo) {

        checkIfTokenExpired(refreshToken);

        updateRefreshTokenEntity(refreshToken, deviceInfo);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void throwExceptionIfUserCanNotDeleteRefreshToken(RefreshToken refreshToken, Long userId) {
        if (!refreshToken.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can't end this session");
        }
    }

    @Transactional
    public void deleteAllUsersRefreshTokens(User user) {
        List<RefreshToken> tokens = getRefreshTokensByUser(user);

        refreshTokenRepository.deleteAll(tokens);
    }

    @Transactional
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional
    public void deleteByToken(String refreshToken) {
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByToken(refreshToken);

        if (tokenOptional.isEmpty()) {
            return;
        }

        RefreshToken token = tokenOptional.get();

        refreshTokenRepository.delete(token);
    }

    @Transactional
    public void deleteUserExpiredTokens(User user) {
        refreshTokenRepository.removeByUserAndExpiredAtBefore(user, Instant.now());
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
            throw new UnauthorizedException("Unauthorized");
        }
    }

}