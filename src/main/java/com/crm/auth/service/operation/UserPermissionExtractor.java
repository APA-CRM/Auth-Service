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

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionExtractor {

    private final UserPermissionService userPermissionService;
    private final UserPermissionSaver userPermissionSaver;

    private final MainClient mainClient;

    public UserPermission getUserPermission(Long organizationId, Long userId) {
        return userPermissionService.getUserPermission(organizationId, userId).
                orElseGet(() -> lockAndGetUserPermission(organizationId, userId));
    }

    private UserPermission lockAndGetUserPermission(Long organizationId, Long userId) {
        Lock lock = userPermissionService.getUserPermissionLock(organizationId, userId);

        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                return userPermissionService.getUserPermission(organizationId, userId)
                        .orElseThrow(() -> new ForbiddenException("Access controls not found"));
            }

            try {
                return userPermissionService.getUserPermission(organizationId, userId)
                        .orElseGet(() ->
                                fetchUserPermissionFromMainServiceAndSave(organizationId, userId)
                                        .orElseThrow(() -> new ForbiddenException("Access controls not found")));
            } finally {
                lock.unlock();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while acquiring permission lock", e);
        }
    }

    private Optional<UserPermission> fetchUserPermissionFromMainServiceAndSave(Long organizationId, Long userId) {
        log.debug("User permission not found in cache: Organization = {}, User = {}",
                organizationId, userId
        );

        OrganizationUserRolesResponse organizationUserRoles;

        try {
            organizationUserRoles =
                    mainClient.getOrganizationUserRoles(organizationId, userId);
        } catch (NotFoundException e) {
            return Optional.empty();
        }

        UserPermission userPermission = userPermissionSaver.saveUserPermission(
                organizationId, userId, organizationUserRoles.getRolesId()
        );

        return Optional.of(userPermission);
    }
}
