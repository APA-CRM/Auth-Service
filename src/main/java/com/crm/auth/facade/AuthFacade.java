package com.crm.auth.facade;

import com.crm.auth.dto.request.RefreshJwtTokenRequest;
import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.service.AuthenticationService;
import com.crm.auth.service.AuthorizationService;
import com.crm.auth.service.operation.RefreshTokenRefresher;
import com.crm.sharedlib.core.annotations.Facade;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.utils.OrganizationIdExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthorizationService authorizationService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenRefresher refreshTokenRefresher;

    public JwtAuthenticationResponse signIn(SignInRequest request, String deviceInfo) {
        return authenticationService.signIn(request, deviceInfo);
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request, String deviceInfo) {
        return authenticationService.signUp(request, deviceInfo);
    }

    public JwtAuthenticationResponse refreshJwtToken(RefreshJwtTokenRequest request, String deviceInfo) {
        return refreshTokenRefresher.refreshJwtToken(request, deviceInfo);
    }

    // TODO: Move to the Internal API
    public AuthResponse authorize(String authorizationHeader) {
        String token = authorizationService.getTokenAndValidate(authorizationHeader);

        return authorizationService.authorize(token);
    }

    // TODO: Move to the Internal API
    public AuthResponse authorizeAndCheckAccess(
            String authorizationHeader, HttpServletRequest servletRequest,
            AuthorizationRequest request
    ) {
        Long organizationId = OrganizationIdExtractor
                .extractOrganizationIdFromRequest(servletRequest, request.getUri());

        String token = authorizationService.getTokenAndValidate(authorizationHeader, organizationId);

        return authorizationService.authorizeAndCheckAccess(token, organizationId, request);
    }

}