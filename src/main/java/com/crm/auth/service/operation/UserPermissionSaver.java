package com.crm.auth.service.operation;

import com.crm.auth.mapper.AccessControlMapper;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.auth.persistance.entity.redis.UserPermission;
import com.crm.auth.service.RoleService;
import com.crm.auth.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermissionSaver {

    private final RoleService roleService;
    private final UserPermissionService userPermissionService;

    private final AccessControlMapper accessControlMapper;

    public UserPermission saveUserPermission(Long organizationId, Long userId, List<Long> rolesId) {

        List<Role> roles =
                roleService.getRolesById(rolesId);

        List<ResourcePermission> resourcePermission =
                accessControlMapper.toResourcePermission(roles);

        return userPermissionService.saveUserPermission(
                organizationId, userId,
                resourcePermission
        );
    }

}
