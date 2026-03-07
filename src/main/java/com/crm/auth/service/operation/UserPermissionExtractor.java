package com.crm.auth.service.operation;

import com.crm.auth.feign.MainClient;
import com.crm.auth.service.UserPermissionService;
import com.crm.sharedlib.core.dto.response.OrganizationUserRolesResponse;
import com.crm.sharedlib.core.exception.ForbiddenException;
import com.crm.sharedlib.core.exception.NotFoundException;
import com.crm.sharedlib.rbac.dto.UserPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionExtractor {

    private final UserPermissionService userPermissionService;
    private final UserPermissionSaver userPermissionSaver;

    private final MainClient mainClient;

    public UserPermission getUserPermission(Long organizationId, Long userId) {
        return userPermissionService.getUserPermission(organizationId, userId).
                orElseGet(() -> fetchUserPermissionFromMainServiceAndSave(organizationId, userId));
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
