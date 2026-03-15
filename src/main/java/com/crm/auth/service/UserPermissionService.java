package com.crm.auth.service;

import com.crm.auth.cache.UserPermissionRepository;
import com.crm.sharedlib.rbac.dto.ResourcePermission;
import com.crm.sharedlib.rbac.dto.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserPermissionService {

    private final UserPermissionRepository repository;

    public UserPermission saveUserPermission(
            Long organizationId, Long userId,
            List<ResourcePermission> resourcePermissions
    ) {
        UserPermission userPermission = new UserPermission(resourcePermissions);

        repository.save(organizationId, userId, userPermission);

        return userPermission;
    }

    public Optional<UserPermission> getUserPermission(Long organizationId, Long userId) {
        return repository.findById(organizationId, userId);
    }

}
