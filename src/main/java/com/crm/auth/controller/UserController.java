package com.crm.auth.controller;

import com.crm.auth.dto.request.UserUpdateRequest;
import com.crm.auth.facade.UserFacade;
import com.crm.sharedlib.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.crm.sharedlib.consts.CrmConstants.USER_ID_HEADER_NAME;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade facade;

    @GetMapping("/me")
    public UserResponse getUser(
            @RequestHeader(USER_ID_HEADER_NAME)
            Long userId
    ) {
        return facade.getUserById(userId);
    }

    @GetMapping
    public List<UserResponse> getUsersByFullName(
            @RequestParam("fullName") String fullName
    ) {
        return facade.getUsersByFullName(fullName);
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(
            @PathVariable("userId") Long userId
    ) {
        return facade.getUserById(userId);
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(
            @RequestHeader(USER_ID_HEADER_NAME) Long authUserId,
            @PathVariable("id") Long targetUserId,
            @RequestBody UserUpdateRequest updates
    ) {
        return facade.updateUser(targetUserId, authUserId, updates);
    }

}
