package com.crm.auth.facade.internal;

import com.crm.auth.service.AuthorizationService;
import com.crm.sharedlib.core.annotations.Facade;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.utils.OrganizationIdExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class InternalAuthFacade {

    private final AuthorizationService authorizationService;

    public AuthResponse authorize(String authorizationHeader) {
        String token = authorizationService.getTokenAndValidate(authorizationHeader);

        return authorizationService.authorize(token);
    }

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
