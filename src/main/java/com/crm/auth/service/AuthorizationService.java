package com.crm.auth.service;

import com.crm.auth.dto.JwtPayload;
import com.crm.auth.persistance.entity.redis.UserPermission;
import com.crm.auth.service.checker.PermissionChecker;
import com.crm.auth.service.operation.UserPermissionExtractor;
import com.crm.auth.utils.JwtUtils;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
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

    public AuthResponse authorize(String authorizationHeader) {
        JwtPayload payload = getToken(authorizationHeader);

        return new AuthResponse(payload.getId(), payload.getLogin());
    }

    public AuthResponse authorizeAndCheckAccess(
            String authorizationHeader, Long organizationId,
            AuthorizationRequest request
    ) {
        JwtPayload payload = getToken(authorizationHeader, organizationId);

        UserPermission userPermission =
                // TODO: Fix casting int to long
                userPermissionExtractor.getUserPermission(organizationId, (long) payload.getId());

        permissionChecker.checkUserPermission(userPermission, request);

        return new AuthResponse(payload.getId(), payload.getLogin());
    }

    private JwtPayload getToken(String authorizationHeader, Long organizationId) {
        if (isNull(organizationId)) {
            throw new UnauthorizedException("Unauthorized");
        }

        return getToken(authorizationHeader);
    }

    private JwtPayload getToken(String authorizationHeader) {
        String token = JwtUtils.getJwtTokenFromAuthorizationHeader(authorizationHeader)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));

        return jwtService.getPayloadFromJwtToken(token);
    }

}
