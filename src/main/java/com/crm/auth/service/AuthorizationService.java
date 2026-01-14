package com.crm.auth.service;

import com.crm.auth.feign.MainClient;
import com.crm.auth.persistance.entity.redis.UserPermission;
import com.crm.auth.service.checker.PermissionChecker;
import com.crm.auth.service.operation.UserPermissionSaver;
import com.crm.auth.utils.JwtUtils;
import com.crm.sharedlib.core.dto.request.AuthorizationRequest;
import com.crm.sharedlib.core.dto.response.AuthResponse;
import com.crm.sharedlib.core.dto.response.OrganizationUserRolesResponse;
import com.crm.sharedlib.core.exception.ForbiddenException;
import com.crm.sharedlib.core.exception.NotFoundException;
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
    private final UserPermissionService userPermissionService;
    private final UserPermissionSaver userPermissionSaver;
    private final PermissionChecker permissionChecker;

    private final MainClient mainClient;

    public String getTokenAndValidate(String authorizationHeader, Long organizationId) {
        if (isNull(organizationId)) {
            throw new UnauthorizedException("Unauthorized");
        }

        return getTokenAndValidate(authorizationHeader);
    }

    public String getTokenAndValidate(String authorizationHeader) {
        String token = JwtUtils.getJwtTokenFromAuthorizationHeader(authorizationHeader)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));

        boolean expired = jwtService.isExpired(token);

        if (expired) {
            throw new UnauthorizedException("Unauthorized");
        }

        return token;
    }

    public AuthResponse authorize(String token) {
        return jwtService.getPayloadFromJwtToken(token);
    }

    public AuthResponse authorizeAndCheckAccess(String token, Long organizationId, AuthorizationRequest request) {

        AuthResponse payload = jwtService.getPayloadFromJwtToken(token);

        Long userId = Long.valueOf(payload.getId());

        UserPermission userPermission =
                userPermissionService.getUserPermission(organizationId, userId).
                        orElseGet(() -> fetchUserPermissionFromMainServiceAndSave(organizationId, userId));

        permissionChecker.checkUserPermission(userPermission, request);

        return payload;
    }

    private UserPermission fetchUserPermissionFromMainServiceAndSave(Long organizationId, Long userId) {
        log.debug("User permission not found in cache: Organization = {}, User = {}",
                organizationId, userId
        );

        OrganizationUserRolesResponse organizationUserRoles;

        try {
            organizationUserRoles =
                    mainClient.getOrganizationUserRoles(organizationId, userId);
        } catch (NotFoundException e) {
            throw new ForbiddenException("Access controls not found");
        }

        return userPermissionSaver.saveUserPermission(
                organizationId, userId, organizationUserRoles.getRolesId()
        );
    }

}
