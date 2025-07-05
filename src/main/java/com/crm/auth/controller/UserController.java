package com.crm.auth.controller;

import com.crm.auth.dto.request.UserRequest;
import com.crm.auth.facade.UserFacade;
import com.crm.sharedlib.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader("X-User-Id") Long authUserId,
            @PathVariable("id") Long targetUserId,
            @RequestBody UserRequest request
    ) {
        if (!authUserId.equals(targetUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only update your own profile.");
        }

        UserResponse updatedUser = facade.updateUser(targetUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

}
