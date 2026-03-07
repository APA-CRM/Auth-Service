package com.crm.auth.service;

import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.exception.UnauthorizedException;
import com.crm.sharedlib.rbac.dto.JwtPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final JwtService jwtService;

    public AuthResponse authorize(AuthorizationRequest request) {
        JwtPayload payload = getAccessTokenPayload(request.getAccessToken());

        return new AuthResponse(payload.getId(), payload.getLogin());
    }

    private JwtPayload getAccessTokenPayload(String accessToken) {
        if (isNull(accessToken)) {
            throw new UnauthorizedException("Unauthorized");
        }

        return jwtService.getPayloadFromJwtToken(accessToken);
    }

}
