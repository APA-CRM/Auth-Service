package com.crm.auth.service;

import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.enums.TokenType;
import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.operation.UserCreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final UserCreatorService userCreatorService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationResponse authenticateUser(User user, String deviceInfo) {
        RefreshToken refreshToken = refreshTokenService.getRefreshTokenBySignInRequest(user, deviceInfo);

        String token = jwtService.generateToken(user.getId(), user.getLogin());

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest, String deviceInfo) {

        User user = userService.validateUserForSignInRequest(signInRequest);

        return authenticateUser(user, deviceInfo);
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request, String deviceInfo) {

        User user = userCreatorService.createUser(request);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(deviceInfo, user);

        String token = jwtService.generateToken(user.getId(), user.getLogin());

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }

}
