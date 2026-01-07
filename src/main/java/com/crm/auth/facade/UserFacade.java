package com.crm.auth.facade;

import com.crm.auth.dto.request.UserUpdateRequest;
import com.crm.auth.mapper.UserMapper;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.UserService;
import com.crm.sharedlib.core.annotations.Facade;
import com.crm.sharedlib.core.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserResponse getUserById(Long userId) {
        User user = userService.getUserByIdOrThrowException(userId);

        return userMapper.toDto(user);
    }

    public List<UserResponse> getUsersByFullName(String fullName) {
        List<User> users = userService.getUsersByFullNameStartsWith(fullName.trim());

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponse updateUser(Long userId, Long authUserId, UserUpdateRequest request) {

        User updated = userService.updateUserFromRequest(userId, authUserId, request);

        return userMapper.toDto(updated);
    }

}
