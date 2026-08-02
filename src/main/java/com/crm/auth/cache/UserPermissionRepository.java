package com.crm.auth.cache;

import com.crm.sharedlib.rbac.dto.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.crm.sharedlib.rbac.constants.CacheConstants.USER_PERMISSION_FORMAT_KEY;

@Repository
@RequiredArgsConstructor
public class UserPermissionRepository {

    private final static String FAILURE_SUFFIX_KEY = ":failure";

    private final RedisTemplate<String, UserPermission> userPermissionRedisTemplate;
    private final RedisTemplate<String, String> lockRedisTemplate;

    private final RedisLockRegistry lockRegistry;

    @Value("${app.user-permission.ttl}")
    private Integer userPermissionTtl;

    @Value("${app.user-permission.failure-lock.ttl}")
    private Integer userPermissionFailureLockTtl;

    public void save(
            Long organizationId,
            Long userId,
            UserPermission userPermission
    ) {
        String key = getFormatedKey(organizationId, userId);

        userPermissionRedisTemplate
                .opsForValue()
                .set(key, userPermission, userPermissionTtl, TimeUnit.SECONDS);
    }

    public void lockFailure(Long organizationId, Long userId, Exception exception) {
        String key = getFormatedKey(organizationId, userId) + FAILURE_SUFFIX_KEY;

        String value = exception.getClass().getSimpleName();

        lockRedisTemplate.opsForValue().set(
                key, value, userPermissionFailureLockTtl, TimeUnit.SECONDS
        );
    }

    public boolean isFailureLockExists(Long organizationId, Long userId) {
        String key = getFormatedKey(organizationId, userId) + FAILURE_SUFFIX_KEY;

        return lockRedisTemplate.opsForValue().get(key) != null;
    }

    public Optional<UserPermission> findById(Long organizationId, Long userId) {
        return Optional.ofNullable(
                userPermissionRedisTemplate
                        .opsForValue()
                        .get(getFormatedKey(organizationId, userId))
        );
    }


    private String getFormatedKey(Long organizationId, Long userId) {
        return USER_PERMISSION_FORMAT_KEY.formatted(organizationId, userId);
    }

    public Lock getUserPermissionLock(Long organizationId, Long userId) {
        return lockRegistry.obtain(getFormatedKey(organizationId, userId));
    }

}
