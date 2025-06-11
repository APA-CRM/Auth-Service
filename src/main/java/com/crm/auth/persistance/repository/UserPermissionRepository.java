package com.crm.auth.persistance.repository;

import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.auth.persistance.entity.redis.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UserPermissionRepository {

    private final RedisTemplate<String, UserPermission> redisTemplate;

    @Value("${app.redis.user-permission.ttl}")
    private Integer userPermissionTtl;

    public UserPermission save(
            Long organizationId,
            Long userId,
            List<ResourcePermission> resourcePermission
    ) {
        String key = getFormatedKey(organizationId, userId);

        UserPermission value = new UserPermission(resourcePermission);

        redisTemplate
                .opsForValue()
                .set(key, value, userPermissionTtl, TimeUnit.SECONDS);

        return value;
    }

    public UserPermission findById(Long organizationId, Long userId) {
        return redisTemplate
                .opsForValue()
                .get(getFormatedKey(organizationId, userId));
    }


    private String getFormatedKey(Long organizationId, Long userId) {
        return "organization:%d:user:%d".formatted(organizationId, userId);
    }

}
