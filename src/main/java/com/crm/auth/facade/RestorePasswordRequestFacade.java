package com.crm.auth.facade;

import com.crm.auth.dto.request.RestorePasswordRequest;
import com.crm.auth.dto.request.VerificationCodeRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.dto.response.RestorePasswordResponse;
import com.crm.auth.mapper.PasswordRestoreRequestMapper;
import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.AuthenticationService;
import com.crm.auth.service.operation.PasswordRecoveryService;
import com.crm.sharedlib.core.annotations.Facade;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Facade
@RequiredArgsConstructor
public class RestorePasswordRequestFacade {

    private final AuthenticationService authenticationService;
    private final PasswordRecoveryService passwordRecoveryService;

    private final PasswordRestoreRequestMapper passwordRequestMapper;

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
