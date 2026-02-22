package com.crm.auth.facade.internal;

import com.crm.auth.service.AuthorizationService;
import com.crm.sharedlib.core.annotations.Facade;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.request.AuthorizationWithUriAndHttpMethodRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.utils.OrganizationIdExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class InternalAuthFacade {

    private final AuthorizationService authorizationService;

    public AuthResponse authorize(AuthorizationRequest request) {
        return authorizationService.authorize(request);
    }

    public AuthResponse authorizeAndCheckAccess(
            HttpServletRequest servletRequest,
            AuthorizationWithUriAndHttpMethodRequest request
    ) {
        Long organizationId = OrganizationIdExtractor
                .extractOrganizationIdFromRequest(servletRequest, request.getUri());

        return authorizationService.authorizeAndCheckAccess(request.getAccessToken(), organizationId, request);
    }

}
