package com.crm.auth.facade;

import com.crm.auth.dto.response.UserSessionDto;
import com.crm.auth.mapper.UserSessionMapper;
import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.RefreshTokenService;
import com.crm.auth.service.UserService;
import com.crm.sharedlib.core.annotations.Facade;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Facade
@RequiredArgsConstructor
public class UserSessionFacade {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    private final UserSessionMapper userSessionMapper;

    public List<UserSessionDto> getUserSessions(Long userId) {
        User user = userService.getUserByIdOrThrowException(userId);

        List<RefreshToken> tokens = refreshTokenService.getRefreshTokensByUser(user);

        return tokens.stream()
                .map(userSessionMapper::toDto)
                .toList();
    }

    public void endUserSession(UUID sessionId, Long authUserId) {
        RefreshToken refreshToken = refreshTokenService.getRefreshTokenOrThrowException(sessionId);

        refreshTokenService.throwExceptionIfUserCanNotDeleteRefreshToken(refreshToken, authUserId);

        refreshTokenService.delete(refreshToken);
    }

    public void endAllUsersSessions(Long userId) {
        User user = userService.getUserByIdOrThrowException(userId);

        refreshTokenService.deleteAllUsersRefreshTokens(user);
    }

}
