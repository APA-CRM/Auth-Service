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

    private final RedisTemplate<String, UserPermission> userPermissionRedisTemplate;

    private final RedisLockRegistry lockRegistry;

    @Value("${app.redis.user-permission.ttl}")
    private Integer userPermissionTtl;

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
