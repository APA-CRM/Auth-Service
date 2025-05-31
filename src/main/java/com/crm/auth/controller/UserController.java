package com.crm.auth.controller;

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
        return facade.getUser(userId);
    }

    @GetMapping
    public List<UserResponse> getUsersByLogin(
            @RequestParam("login") String login
    ) {
        return facade.getUsersByLogin(login);
    }

}
