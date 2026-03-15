package com.crm.auth.facade.internal;

import com.crm.auth.service.AuthorizationService;
import com.crm.sharedlib.core.annotations.Facade;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class InternalAuthFacade {

    private final AuthorizationService authorizationService;

    public AuthResponse authorize(AuthorizationRequest request) {
        return authorizationService.authorize(request);
    }

}
