package com.crm.auth.controller.internal;

import com.crm.auth.facade.internal.InternalAuthFacade;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/internal/auth")
@RequiredArgsConstructor
public class InternalAuthController {

    private final InternalAuthFacade internalAuthFacade;

    @GetMapping("authorize")
    public AuthResponse authorize(
            @RequestHeader(value = AUTHORIZATION, required = false)
            String authorizationHeader
    ) {
        return internalAuthFacade.authorize(authorizationHeader);
    }

    @PostMapping("/check-access")
    public AuthResponse authorizeAndCheckAccess(
            @RequestHeader(value = AUTHORIZATION, required = false)
            String authorizationHeader,
            @RequestBody AuthorizationRequest request,
            HttpServletRequest servletRequest
    ) {
        return internalAuthFacade.authorizeAndCheckAccess(authorizationHeader, servletRequest, request);
    }

}
