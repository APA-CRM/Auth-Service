package com.crm.auth.controller;

import com.crm.auth.dto.request.*;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.dto.response.RestorePasswordResponse;
import com.crm.auth.facade.AuthFacade;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(
            @Valid
            @RequestBody
            SignInRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return authFacade.signIn(request, deviceInfo);
    }

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(
            @Valid
            @RequestBody
            SignUpRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return authFacade.signUp(request, deviceInfo);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationResponse refreshJwtToken(
            @Valid
            @RequestBody
            RefreshJwtTokenRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return authFacade.refreshJwtToken(request, deviceInfo);
    }

    @PostMapping("/restore-password-request")
    public RestorePasswordResponse createRequestToRestorePassword(
            @Valid
            @RequestBody
            RestorePasswordRequest request
    ) {
        return authFacade.createRequestToRestorePassword(request);
    }

    @PutMapping("/restore-password-request/{requestId}/restore-password")
    public JwtAuthenticationResponse restorePasswordByVerificationCode(
            @PathVariable("requestId")
            UUID requestId,
            @Valid @RequestBody
            VerificationCodeRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return authFacade.restorePasswordByVerificationCode(requestId, request, deviceInfo);
    }

    // TODO: Move to the Internal API
    @GetMapping("authorize")
    public AuthResponse authorize(
            @RequestHeader(value = AUTHORIZATION, required = false)
            String authorizationHeader
    ) {
        return authFacade.authorize(authorizationHeader);
    }

    // TODO: Move to the Internal API
    @PostMapping("/check-access")
    public AuthResponse authorizeAndCheckAccess(
            @RequestHeader(value = AUTHORIZATION, required = false)
            String authorizationHeader,
            @RequestBody AuthorizationRequest request,
            HttpServletRequest servletRequest
    ) {
        return authFacade.authorizeAndCheckAccess(authorizationHeader, servletRequest, request);
    }

}
