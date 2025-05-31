package com.crm.auth.facade.internal;

import com.crm.sharedlib.annotations.Facade;
import com.crm.auth.mapper.UserMapper;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.UserFilterService;
import com.crm.auth.service.UserService;
import com.crm.sharedlib.dto.request.UserWithRolesFilterRequest;
import com.crm.sharedlib.dto.response.UserResponse;
import com.crm.sharedlib.dto.response.UserWithRoleResponse;
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
