package com.crm.auth.facade;

import com.crm.auth.dto.request.RefreshJwtTokenRequest;
import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.enums.TokenType;
import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.AuthenticationService;
import com.crm.auth.service.JwtService;
import com.crm.auth.service.RefreshTokenService;
import com.crm.auth.service.UserService;
import com.crm.auth.service.operation.UserCreatorService;
import com.crm.sharedlib.annotations.Facade;
import com.crm.sharedlib.dto.request.AuthorizationRequest;
import com.crm.sharedlib.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final UserService userService;

    private final UserCreatorService userCreatorService;

    private final AuthenticationService authenticationService;

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest, String deviceInfo) {

        User user = userService.validateUserForSignInRequest(signInRequest);

        RefreshToken refreshToken = refreshTokenService.getRefreshTokenBySignInRequest(user, deviceInfo);

        String token = jwtService.generateToken(user.getId(), user.getLogin());

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request, String deviceInfo) {

        User user = userCreatorService.createUser(request);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(deviceInfo, user);

        String token = jwtService.generateToken(user.getId(), user.getLogin());

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

    public JwtAuthenticationResponse refreshJwtToken(RefreshJwtTokenRequest request, String deviceInfo) {

        RefreshToken refreshToken = refreshTokenService
                .validateAndRecreateRefreshToken(request.getRefreshToken(), deviceInfo);

        User user = refreshToken.getUser();

        String token = jwtService.generateToken(user.getId(), user.getLogin());

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

    public AuthResponse authorize(
            String authorizationHeader, Long organizationId,
            AuthorizationRequest request
    ) {
        String token = authenticationService.getTokenAndValidate(authorizationHeader, organizationId);

        return authenticationService.authorize(token, organizationId, request);
    }

}