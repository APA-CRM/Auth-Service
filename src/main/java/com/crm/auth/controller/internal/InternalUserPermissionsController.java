package com.crm.auth.controller.internal;

import com.crm.auth.facade.internal.InternalUserPermissionsFacade;
import com.crm.sharedlib.rbac.dto.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalUserPermissionsController {

    private final InternalUserPermissionsFacade facade;

    @GetMapping("/users/{userId}/organizations/{organizationId}/persmissions")
    public UserPermission getUserPermission(
            @PathVariable("userId") Long userId,
            @PathVariable("organizationId") Long organizationId
    ) {
        return facade.getUserPermission(userId, organizationId);
    }

}
