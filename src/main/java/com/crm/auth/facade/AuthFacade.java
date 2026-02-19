package com.crm.auth.facade;

import com.crm.auth.dto.request.*;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.dto.response.RestorePasswordResponse;
import com.crm.auth.mapper.PasswordRestoreRequestMapper;
import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.AuthenticationService;
import com.crm.auth.service.operation.PasswordRecoveryService;
import com.crm.auth.service.operation.RefreshTokenProvider;
import com.crm.sharedlib.core.annotations.Facade;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Facade
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthenticationService authenticationService;
    private final RefreshTokenProvider refreshTokenProvider;

    private final PasswordRecoveryService passwordRecoveryService;

    private final PasswordRestoreRequestMapper passwordRequestMapper;

    public JwtAuthenticationResponse signIn(SignInRequest request, String deviceInfo) {
        return authenticationService.signIn(request, deviceInfo);
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request, String deviceInfo) {
        return authenticationService.signUp(request, deviceInfo);
    }

    public JwtAuthenticationResponse refreshJwtToken(RefreshJwtTokenRequest request, String deviceInfo) {
        return refreshTokenProvider.refreshJwtToken(request, deviceInfo);
    }

    public RestorePasswordResponse createRequestToRestorePassword(RestorePasswordRequest restorePasswordRequest) {
        PasswordRestoreRequest restoreRequest =
                passwordRecoveryService.createPasswordRestoreRequest(restorePasswordRequest);

        return passwordRequestMapper.toResponse(restoreRequest);
    }

    public RestorePasswordResponse resendVerificationCode(UUID requestId) {
        PasswordRestoreRequest restoreRequest = passwordRecoveryService.resendVerificationCode(requestId);

        return passwordRequestMapper.toResponse(restoreRequest);
    }

    public JwtAuthenticationResponse restorePasswordByVerificationCode(
            UUID requestId, VerificationCodeRequest request, String deviceInfo
    ) {
        User user = passwordRecoveryService.checkVerificationCodeAndGetUser(requestId, request);

        return authenticationService.authenticateUser(user, deviceInfo);
    }

}