package com.crm.auth.service.filter;

import com.crm.auth.filter.UserFilter;
import com.crm.auth.mapper.UserMapper;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.RoleService;
import com.crm.sharedlib.core.dto.request.UserWithRolesFilterRequest;
import com.crm.sharedlib.core.dto.response.UserAndRoles;
import com.crm.sharedlib.core.dto.response.UserWithRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserFilterService {

    private final UserFilter userFilter;

    private final RoleService roleService;

    private final UserMapper userMapper;

    public PagedModel<UserWithRoleResponse> filterUsersOfOrganization(UserWithRolesFilterRequest request) {

        Page<User> filteredUsers = userFilter.filter(request);

        if (nonNull(request.getUserAndRoles())) {
            return new PagedModel<>(adjustRolesForUsers(filteredUsers, request));
        }

        return new PagedModel<>(filteredUsers
                .map(user -> userMapper.toUserWithRoleResponse(user, null)));
    }

    private Map<Long, List<Long>> mapUserRoles(List<UserAndRoles> orgUsers) {
        return orgUsers.stream()
                .collect(Collectors.toMap(
                        UserAndRoles::getUserId,
                        UserAndRoles::getRolesIds
                ));
    }

    private Page<UserWithRoleResponse> adjustRolesForUsers(
            Page<User> filteredUsers,
            UserWithRolesFilterRequest request
    ) {
        List<UserAndRoles> orgUsers = request.getUserAndRoles();

        Map<Long, List<Long>> userRolesMap = mapUserRoles(orgUsers);

        Set<Long> allRoleIds = userRolesMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Map<Long, Role> roleMap = roleService.getRolesById(allRoleIds).stream()
                .collect(Collectors.toMap(Role::getId, Function.identity()));

        return mapUsersToResponse(filteredUsers, userRolesMap, roleMap);
    }

    private Page<UserWithRoleResponse> mapUsersToResponse(
            Page<User> users,
            Map<Long, List<Long>> userRolesMap,
            Map<Long, Role> roleMap
    ) {
        return users.map(user -> {
            List<Role> roles = userRolesMap.getOrDefault(user.getId(), Collections.emptyList()).stream()
                    .map(roleMap::get)
                    .toList();
            return userMapper.toUserWithRoleResponse(user, roles);
        });
    }
}
