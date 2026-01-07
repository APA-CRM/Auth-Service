package com.crm.auth.facade.internal;

import com.crm.auth.mapper.UserMapper;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.UserService;
import com.crm.auth.service.filter.UserFilterService;
import com.crm.sharedlib.core.annotations.Facade;
import com.crm.sharedlib.core.dto.request.UserWithRolesFilterRequest;
import com.crm.sharedlib.core.dto.response.UserResponse;
import com.crm.sharedlib.core.dto.response.UserWithRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;

@Facade
@RequiredArgsConstructor
public class InternalUsersFacade {

    private final UserService userService;

    private final UserFilterService filterService;

    private final UserMapper userMapper;

    public PagedModel<UserWithRoleResponse> filterUsersOfOrganization(
            UserWithRolesFilterRequest request
    ) {
        return filterService.filterUsersOfOrganization(request);
    }

    public UserResponse getUserById(Long userId) {
        User user = userService.getUserByIdOrThrowException(userId);

        return userMapper.toDto(user);
    }

}
