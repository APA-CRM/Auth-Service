package com.crm.auth.service.consumer;

import com.crm.auth.BaseIntegrationTestWithRabbitMQ;
import com.crm.auth.cache.UserPermissionRepository;
import com.crm.sharedlib.core.dto.amqp.OrgUserRoleChangedEvent;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;

import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.ORGANIZATION_USER_ROLES_SYNC_QUEUE;

@Sql(scripts = "classpath:sql/insertTestRoles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/deleteTestRoles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UpdateOrganizationUserPermissionTest extends BaseIntegrationTestWithRabbitMQ {

    @MockitoBean
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Update organization user permission expected success")
    @SneakyThrows
    public void updateOrganizationUserPermissionExpectedSuccess() {
        OrgUserRoleChangedEvent message = OrgUserRoleChangedEvent.builder()
                .rolesIs(Collections.singletonList(200L))
                .userId(100L)
                .organizationId(1L)
                .build();

        rabbitTemplate.convertAndSend(ORGANIZATION_USER_ROLES_SYNC_QUEUE, message);

        // Wait, because test is async
        Thread.sleep(2000);

        Mockito.verify(userPermissionRepository, Mockito.atLeastOnce())
                .save(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());

    }

}