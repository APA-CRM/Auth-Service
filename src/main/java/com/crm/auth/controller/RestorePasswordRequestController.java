package com.crm.auth.controller;

import com.crm.auth.dto.request.RestorePasswordRequest;
import com.crm.auth.dto.request.VerificationCodeRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.dto.response.RestorePasswordResponse;
import com.crm.auth.facade.RestorePasswordRequestFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.USER_AGENT;

@RestController
@RequestMapping("/api/restore-password-request")
@RequiredArgsConstructor
public class RestorePasswordRequestController {

    private final RestorePasswordRequestFacade facade;

    @PostMapping
    public RestorePasswordResponse createRequestToRestorePassword(
            @Valid
            @RequestBody
            RestorePasswordRequest request
    ) {
        return facade.createRequestToRestorePassword(request);
    }

    @PatchMapping("/{requestId}/resend")
    public RestorePasswordResponse resendVerificationCode(
            @PathVariable("requestId") UUID requestId
    ) {
        return facade.resendVerificationCode(requestId);
    }

    @PutMapping("/{requestId}/restore-password")
    public JwtAuthenticationResponse restorePasswordByVerificationCode(
            @PathVariable("requestId")
            UUID requestId,
            @Valid @RequestBody
            VerificationCodeRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return facade.restorePasswordByVerificationCode(requestId, request, deviceInfo);
    }

}
