package com.crm.auth.service;

import com.crm.auth.BaseIntegrationTestWithRedis;
import com.crm.auth.cache.UserPermissionRepository;
import com.crm.auth.feign.MainClient;
import com.crm.auth.service.operation.UserPermissionExtractor;
import com.crm.sharedlib.core.dto.response.OrganizationUserRolesResponse;
import com.crm.sharedlib.core.enums.Action;
import com.crm.sharedlib.core.enums.Resource;
import com.crm.sharedlib.core.exception.ForbiddenException;
import com.crm.sharedlib.core.exception.NotFoundException;
import com.crm.sharedlib.rbac.dto.ResourcePermission;
import com.crm.sharedlib.rbac.dto.UserPermission;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserPermissionExtractorTest extends BaseIntegrationTestWithRedis {

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserPermissionExtractor userPermissionExtractor;

    @MockitoBean
    private MainClient mainClient;

    @Test
    @DisplayName("Get user permission when its exists expected success")
    public void getUserPermissionWhenExistsExpectedSuccess() {
        final long organizationId = 100L, userId = 1L;

        ResourcePermission permission = new ResourcePermission(
                Resource.ALL, Collections.singletonList(Action.ALL)
        );

        UserPermission expectedUserPermission = new UserPermission(List.of(permission));

        userPermissionRepository.save(organizationId, userId, expectedUserPermission);

        UserPermission userPermission =
                assertDoesNotThrow(() -> userPermissionExtractor.getUserPermission(organizationId, userId));

        assertThat(userPermission)
                .usingRecursiveComparison()
                .isEqualTo(expectedUserPermission);
    }

    @Test
    @DisplayName("Get user permission when its not exists and not found expected success")
    public void getUserPermissionWhenNotExistsAndNotFoundExpectedSuccess() {
        final long organizationId = 100L, userId = 1L;

        Mockito.when(mainClient.getOrganizationUserRoles(organizationId, userId))
                .thenThrow(new NotFoundException("User roles is not found"));

        assertThrows(ForbiddenException.class, () -> userPermissionExtractor.getUserPermission(organizationId, userId));
    }

    @Test
    @DisplayName("Get user permission when its not exists and two threads concurrently get the permission, but only one thread acquire the lock")
    @Sql(scripts = "classpath:sql/insertTestRoles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/deleteTestRoles.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getUserPermissionWhenNotExists_TwoThreadsTryingToGetUsePermission_ExpectedSuccess() {
        final long organizationId = 100L, userId = 1L, roleId = 100L;

        OrganizationUserRolesResponse response = new OrganizationUserRolesResponse();
        response.setOrganizationId(organizationId);
        response.setUserId(userId);
        response.setRolesId(Collections.singletonList(roleId));

        Mockito.when(mainClient.getOrganizationUserRoles(organizationId, userId))
                .thenReturn(response);

        final int numberOfThreads = 2;

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch endSignal = new CountDownLatch(numberOfThreads);

        Callable<Optional<UserPermission>> callable = getCallable(organizationId, userId, startSignal, endSignal);

        Result result = executeCallable(numberOfThreads, callable, startSignal, endSignal);

        // Assert
        try {
            Optional<UserPermission> firstUserPermissionOptional = result.firstFuture.get();
            Optional<UserPermission> secondUserPermissionOptional = result.secondFuture.get();

            assertTrue(firstUserPermissionOptional.isPresent(), "First future has not returned the user permission");
            assertTrue(secondUserPermissionOptional.isPresent(), "Second future has not returned the user permission");

            UserPermission firstUserPermission = firstUserPermissionOptional.get();
            UserPermission secondUserPermission = secondUserPermissionOptional.get();

            assertThat(firstUserPermission)
                    .usingRecursiveComparison()
                    .isEqualTo(secondUserPermission);

            // Only one thread should invoke the main client
            Mockito.verify(mainClient, Mockito.atMostOnce())
                    .getOrganizationUserRoles(organizationId, userId);
        } catch (InterruptedException | ExecutionException e) {
            fail("Error has occurred while getting user permissions from the futures");
        }
    }

    @Test
    @DisplayName("Get user permission when its not exists and not found, two threads concurrently get the permission, " +
            "but only one thread acquire the lock and both not found the permission")
    public void getUserPermissionWhenNotExistsAndNotFound_TwoThreadsTryingToGetUserPermission_ExpectedFail() {
        final long organizationId = 100L, userId = 1L;

        Mockito.when(mainClient.getOrganizationUserRoles(organizationId, userId))
                .thenThrow(new NotFoundException("User roles is not found"));

        final int numberOfThreads = 2;

        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch endSignal = new CountDownLatch(numberOfThreads);

        Callable<Optional<UserPermission>> callable = getCallable(organizationId, userId, startSignal, endSignal);

        Result result = executeCallable(numberOfThreads, callable, startSignal, endSignal);

        // Assert
        try {
            Optional<UserPermission> firstUserPermissionOptional = result.firstFuture().get();
            Optional<UserPermission> secondUserPermissionOptional = result.secondFuture().get();

            assertTrue(firstUserPermissionOptional.isEmpty(), "First future has returned the user permission");
            assertTrue(secondUserPermissionOptional.isEmpty(), "Second future has returned the user permission");

            // Only one thread should invoke the main client
            Mockito.verify(mainClient, Mockito.atMostOnce())
                    .getOrganizationUserRoles(organizationId, userId);
        } catch (InterruptedException | ExecutionException e) {
            fail("Error has occurred while getting user permissions from the futures");
        }
    }

    private Result executeCallable(
            int numberOfThreads, Callable<Optional<UserPermission>> callable,
            CountDownLatch startSignal, CountDownLatch endSignal
    ) {
        Future<Optional<UserPermission>> secondFuture;
        Future<Optional<UserPermission>> firstFuture;

        try (ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads)) {
            log.info("Submit the two threads to the ExecutorService");
            // Submit
            firstFuture = executorService.submit(callable);
            secondFuture = executorService.submit(callable);

            log.info("Start the two threads to get the user permission");
            startSignal.countDown();

            endSignal.await();
            log.info("The two threads has ended their execution");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return new Result(firstFuture, secondFuture);
    }

    private @NonNull Callable<Optional<UserPermission>> getCallable(
            long organizationId, long userId,
            CountDownLatch startSignal, CountDownLatch endSignal
    ) {
        return () -> {
            // Wait until startSignal.countDown() won't be invoked
            startSignal.await();

            try {
                return Optional.of(userPermissionExtractor.getUserPermission(organizationId, userId));
            } catch (Exception e) {
                return Optional.empty();
            } finally {
                endSignal.countDown();
            }
        };
    }

    private record Result(Future<Optional<UserPermission>> firstFuture, Future<Optional<UserPermission>> secondFuture) {
    }
}