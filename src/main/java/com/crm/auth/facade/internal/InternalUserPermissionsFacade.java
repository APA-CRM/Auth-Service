package com.crm.auth.facade.internal;

import com.crm.auth.service.operation.UserPermissionExtractor;
import com.crm.sharedlib.core.annotations.Facade;
import com.crm.sharedlib.rbac.dto.UserPermission;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class InternalUserPermissionsFacade {

    private final UserPermissionExtractor userPermissionExtractor;

    public UserPermission getUserPermission(Long userId, Long organizationId) {
        return userPermissionExtractor.getUserPermission(organizationId, userId);
    }

}
