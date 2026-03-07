package com.crm.auth.controller.internal;

import com.crm.auth.facade.internal.InternalAuthFacade;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final InternalAuthFacade internalAuthFacade;

    @PostMapping("/authorize")
    public AuthResponse authorize(@RequestBody AuthorizationRequest request) {
        return internalAuthFacade.authorize(request);
    }

}
