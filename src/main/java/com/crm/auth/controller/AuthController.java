package com.crm.auth.controller;

import com.crm.auth.dto.request.RefreshJwtTokenRequest;
import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.facade.AuthenticationFacade;
import com.crm.sharedlib.dto.request.AuthorizationRequest;
import com.crm.sharedlib.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.crm.sharedlib.consts.CrmConstants.ORGANIZATION_ID_HEADER_NAME;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(
            @Valid
            @RequestBody
            SignInRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return authenticationFacade.signIn(request, deviceInfo);
    }

    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(
            @Valid
            @RequestBody
            SignUpRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return authenticationFacade.signUp(request, deviceInfo);
    }

    @PostMapping("/refresh")
    public JwtAuthenticationResponse refreshJwtToken(
            @Valid
            @RequestBody
            RefreshJwtTokenRequest request,
            @RequestHeader(USER_AGENT)
            String deviceInfo
    ) {
        return authenticationFacade.refreshJwtToken(request, deviceInfo);
    }

    @PostMapping("/authorize")
    public AuthResponse authorize(
            @RequestHeader(value = AUTHORIZATION, required = false)
            String authorizationHeader,
            @RequestHeader(value = ORGANIZATION_ID_HEADER_NAME, required = false)
            Long organizationId,
            @RequestBody AuthorizationRequest request
    ) {
        return authenticationFacade.authorize(authorizationHeader, organizationId, request);
    }

}
