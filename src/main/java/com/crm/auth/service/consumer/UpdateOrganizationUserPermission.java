package com.crm.auth.service.consumer;

import com.crm.auth.mapper.AccessControlMapper;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.auth.service.RoleService;
import com.crm.auth.service.UserPermissionService;
import com.crm.sharedlib.dto.amqp.OrgUserRoleChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.crm.sharedlib.consts.CrmConstants.ORGANIZATION_USER_ROLES_SYNC_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateOrganizationUserPermission {

    private final RoleService roleService;
    private final UserPermissionService userPermissionService;

    private final AccessControlMapper accessControlMapper;

    @RabbitListener(queues = ORGANIZATION_USER_ROLES_SYNC_QUEUE)
    public void updateOrganizationUserPermission(OrgUserRoleChangedEvent message) {

        log.debug("Starting to process the updated roles of the organization user: " +
                        "User = {}, Organization = {}, Roles = {}",
                message.getUserId(), message.getOrganizationId(), message.getRolesIs()
        );

        List<Role> roles =
                roleService.getRolesById(message.getRolesIs());

        List<ResourcePermission> resourcePermission =
                accessControlMapper.toResourcePermission(roles);

        userPermissionService.saveUserPermission(
                message.getOrganizationId(), message.getUserId(),
                resourcePermission
        );

        log.debug("Updating the roles of the organization user has been successfully completed: " +
                        "User = {}, Organization = {}, Roles = {}",
                message.getUserId(), message.getOrganizationId(), message.getRolesIs()
        );
    }

}
