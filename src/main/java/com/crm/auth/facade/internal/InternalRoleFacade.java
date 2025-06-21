package com.crm.auth.facade.internal;

import com.crm.auth.filter.RoleFilter;
import com.crm.auth.mapper.RoleMapper;
import com.crm.auth.persistance.entity.AccessControl;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.service.AccessControlService;
import com.crm.auth.service.RoleService;
import com.crm.sharedlib.annotations.Facade;
import com.crm.sharedlib.dto.request.CreateRoleRequest;
import com.crm.sharedlib.dto.request.RoleFilterRequest;
import com.crm.sharedlib.dto.request.RoleRequest;
import com.crm.sharedlib.dto.response.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class InternalRoleFacade {

    private final RoleService roleService;

    private final AccessControlService accessControlService;

    private final RoleMapper roleMapper;

    private final RoleFilter roleFilter;

    @Transactional
    public RoleResponse createRole(
            CreateRoleRequest request
    ) {
        Role role = roleService.createRole(request.getName(), request.getIsDeletable());

        List<AccessControl> accessControls =
                accessControlService.createAccessControls(request.getResources(), role);

        return roleMapper.toDto(role);
    }

    public List<RoleResponse> getRolesById(List<Long> rolesId) {
        List<Role> roles = roleService.getRolesById(rolesId);

        return roles.stream()
                .map(roleMapper::toDto)
                .toList();
    }

    public PagedModel<RoleResponse> filterRoles(RoleFilterRequest request) {
        return new PagedModel<>(
                roleFilter.filter(request)
                        .map(roleMapper::toDto)
        );
    }

    @Transactional
    public RoleResponse updateRole(Long roleId, RoleRequest request) {

        Role role = roleService.getRole(roleId);

        List<AccessControl> accessControls =
                accessControlService.createAccessControls(request.getResources(), role);

        role = roleService.updateRole(role, request, accessControls);

        return roleMapper.toDto(role);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleService.getRole(roleId);

        roleService.deleteRole(role);
    }

}
