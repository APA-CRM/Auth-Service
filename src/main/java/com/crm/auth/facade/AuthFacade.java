package com.crm.auth.facade;

import com.crm.auth.dto.request.RefreshTokenRequest;
import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.service.AuthenticationService;
import com.crm.auth.service.operation.RefreshTokenProvider;
import com.crm.sharedlib.core.annotations.Facade;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthenticationService authenticationService;
    private final RefreshTokenProvider refreshTokenProvider;

    public JwtAuthenticationResponse signIn(SignInRequest request, String deviceInfo) {
        return authenticationService.signIn(request, deviceInfo);
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request, String deviceInfo) {
        return authenticationService.signUp(request, deviceInfo);
    }

    public JwtAuthenticationResponse refreshJwtToken(RefreshTokenRequest request, String deviceInfo) {
        return refreshTokenProvider.refreshJwtToken(request, deviceInfo);
    }

    public void logout(RefreshTokenRequest request) {
        authenticationService.logout(request.getRefreshToken());
    }

}