package com.crm.auth.facade;

import com.crm.auth.mapper.UserMapper;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.UserService;
import com.crm.sharedlib.annotations.Facade;
import com.crm.sharedlib.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@Facade
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserResponse getUser(Long userId) {
        User user = userService.getUserByIdOrThrowException(userId);

        return userMapper.toDto(user);
    }

    public List<UserResponse> getUsersByFullName(String fullName) {
        List<User> users = userService.getUsersByFullNameStartsWith(fullName.trim());

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserResponse> getUserById(Long id) {
        List<User> users = Collections.singletonList(userService.getUserByIdOrThrowException(id));

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

}
