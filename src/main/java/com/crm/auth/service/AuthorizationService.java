package com.crm.auth.service;

import com.crm.sharedlib.rbac.dto.JwtPayload;
import com.crm.sharedlib.rbac.dto.UserPermission;
import com.crm.auth.service.checker.PermissionChecker;
import com.crm.auth.service.operation.UserPermissionExtractor;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.request.AuthorizationWithUriAndHttpMethodRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final JwtService jwtService;
    private final PermissionChecker permissionChecker;
    private final UserPermissionExtractor userPermissionExtractor;

    public AuthResponse authorize(AuthorizationRequest request) {
        JwtPayload payload = getAccessTokenPayload(request.getAccessToken());

        return new AuthResponse(payload.getId(), payload.getLogin());
    }

    public AuthResponse authorizeAndCheckAccess(
            String token, Long organizationId,
            AuthorizationWithUriAndHttpMethodRequest request
    ) {
        JwtPayload payload = getAccessTokenPayload(token, organizationId);

        UserPermission userPermission =
                userPermissionExtractor.getUserPermission(organizationId, payload.getId());

        permissionChecker.checkUserPermission(userPermission, request);

        return new AuthResponse(payload.getId(), payload.getLogin());
    }

    private JwtPayload getAccessTokenPayload(String accessToken, Long organizationId) {
        if (isNull(organizationId)) {
            throw new UnauthorizedException("Unauthorized");
        }

        return getAccessTokenPayload(accessToken);
    }

    private JwtPayload getAccessTokenPayload(String accessToken) {
        if (isNull(accessToken)) {
            throw new UnauthorizedException("Unauthorized");
        }

        return jwtService.getPayloadFromJwtToken(accessToken);
    }

}
