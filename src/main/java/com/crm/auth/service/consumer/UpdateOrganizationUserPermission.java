package com.crm.auth.service.consumer;

import com.crm.auth.mapper.AccessControlMapper;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.auth.service.RoleService;
import com.crm.auth.service.UserPermissionService;
import com.crm.sharedlib.dto.amqp.OrgUserRoleChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.crm.sharedlib.consts.CrmConstants.ORGANIZATION_USER_ROLES_SYNC_QUEUE;

@Service
@RequiredArgsConstructor
public class UpdateOrganizationUserPermission {

    private final RoleService roleService;
    private final UserPermissionService userPermissionService;

    private final AccessControlMapper accessControlMapper;

    @RabbitListener(queues = ORGANIZATION_USER_ROLES_SYNC_QUEUE)
    public void updateOrganizationUserPermission(OrgUserRoleChangedEvent message) {

        List<Role> roles =
                roleService.getRolesById(message.getRolesIs());

        List<ResourcePermission> resourcePermission =
                accessControlMapper.toResourcePermission(roles);

        userPermissionService.saveUserPermission(
                message.getOrganizationId(), message.getUserId(),
                resourcePermission
        );
    }

}
