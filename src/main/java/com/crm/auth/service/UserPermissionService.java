package com.crm.auth.service;

import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.auth.persistance.entity.redis.UserPermission;
import com.crm.auth.persistance.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserPermissionRepository repository;

    public void saveUserPermission(
            Long organizationId, Long userId,
            List<ResourcePermission> resourcePermissions
    ) {
        UserPermission userPermission = new UserPermission(resourcePermissions);

        repository.save(organizationId, userId, userPermission);
    }

}
