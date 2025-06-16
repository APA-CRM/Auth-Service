package com.crm.auth.service.consumer;

import com.crm.auth.service.operation.UserPermissionSaver;
import com.crm.sharedlib.dto.amqp.OrgUserRoleChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.crm.sharedlib.consts.CrmConstants.ORGANIZATION_USER_ROLES_SYNC_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateOrganizationUserPermission {

    private final UserPermissionSaver userPermissionSaver;

    @RabbitListener(queues = ORGANIZATION_USER_ROLES_SYNC_QUEUE)
    public void updateOrganizationUserPermission(OrgUserRoleChangedEvent message) {

        log.debug("Starting to process the updated roles of the organization user: " +
                        "User = {}, Organization = {}, Roles = {}",
                message.getUserId(), message.getOrganizationId(), message.getRolesIs()
        );

        userPermissionSaver.saveUserPermission(
                message.getOrganizationId(), message.getUserId(),
                message.getRolesIs()
        );

        log.debug("Updating the roles of the organization user has been successfully completed: " +
                        "User = {}, Organization = {}, Roles = {}",
                message.getUserId(), message.getOrganizationId(), message.getRolesIs()
        );
    }

}
