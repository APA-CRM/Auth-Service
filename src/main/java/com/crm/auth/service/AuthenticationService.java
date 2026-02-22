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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final UserCreatorService userCreatorService;
    private final JwtService jwtService;
    private final DeviceInfoService deviceInfoService;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationResponse authenticateUser(User user, String userAgent) {
        String deviceInfo = deviceInfoService.getDeviceInfoFromUserAgent(userAgent);

        RefreshToken refreshToken = refreshTokenService.getRefreshTokenBySignInRequest(user, deviceInfo);

        String token = jwtService.generateToken(user.getId(), user.getLogin(), deviceInfo);

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest, String userAgent) {

        User user = userService.validateUserForSignInRequest(signInRequest);

        return authenticateUser(user, userAgent);
    }

    @Transactional
    public JwtAuthenticationResponse signUp(SignUpRequest request, String userAgent) {

        User user = userCreatorService.createUser(request);

        String deviceInfo = deviceInfoService.getDeviceInfoFromUserAgent(userAgent);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(deviceInfo, user);

        String token = jwtService.generateToken(user.getId(), user.getLogin(), deviceInfo);

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }

}
