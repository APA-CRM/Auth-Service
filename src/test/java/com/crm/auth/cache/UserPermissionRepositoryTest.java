package com.crm.auth.cache;

import com.crm.auth.BaseIntegrationTest;
import com.crm.sharedlib.core.enums.Action;
import com.crm.sharedlib.core.enums.Resource;
import com.crm.sharedlib.rbac.dto.ResourcePermission;
import com.crm.sharedlib.rbac.dto.UserPermission;
import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserPermissionRepositoryTest extends BaseIntegrationTest {

    private static RedisContainer redisContainer;

    @Autowired
    private UserPermissionRepository repository;

    @BeforeAll
    public static void initRedisContainer() {
        int exposedPort = 6379;

        redisContainer = new RedisContainer("redis:8.4.0-alpine");
        redisContainer.withExposedPorts(exposedPort);
        redisContainer.withLogConsumer(new Slf4jLogConsumer(log));
        redisContainer.start();

        System.setProperty("spring.data.redis.host", redisContainer.getHost());
        System.setProperty("spring.data.redis.port", redisContainer.getFirstMappedPort().toString());
    }

    @AfterAll
    public static void stopRedisContainer() {
        redisContainer.stop();
    }

    @Test
    @DisplayName("Save user permission expected success")
    public void saveUserPermissionExpectedSuccess() {
        ResourcePermission resourcePermission = new ResourcePermission();
        resourcePermission.setResource(Resource.ALL);
        resourcePermission.setActions(List.of(Action.ALL));

        UserPermission userPermission = new UserPermission(Collections.singletonList(resourcePermission));

        assertDoesNotThrow(() -> repository.save(1L, 1L, userPermission));
    }

    @Test
    @DisplayName("Save user permission and get it from the cache exptected success")
    public void saveUserPermissionAndGetItFromCacheExpectedSuccess() {
        ResourcePermission expectedPermission = new ResourcePermission();
        expectedPermission.setResource(Resource.ALL);
        expectedPermission.setActions(List.of(Action.ALL));

        UserPermission userPermission = new UserPermission(Collections.singletonList(expectedPermission));

        long userId = 1L, organizationId = 1L;

        assertDoesNotThrow(() -> repository.save(organizationId, userId, userPermission));

        Optional<UserPermission> optionalUserPermission = assertDoesNotThrow(() -> repository.findById(organizationId, userId));

        assertTrue(optionalUserPermission.isPresent());

        List<ResourcePermission> resourcePermissions = optionalUserPermission.get().getResourcePermissions();

        assertEquals(expectedPermission.getResource(), resourcePermissions.getFirst().getResource());
        assertEquals(expectedPermission.getActions().getFirst(), resourcePermissions.getFirst().getActions().getFirst());
    }

}