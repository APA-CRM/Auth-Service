package com.crm.auth.controller.internal;

import com.crm.auth.dto.response.UserLightResponse;
import com.crm.auth.facade.internal.InternalUsersFacade;
import com.crm.sharedlib.core.dto.request.UserWithRolesFilterRequest;
import com.crm.sharedlib.core.dto.response.UserResponse;
import com.crm.sharedlib.core.dto.response.UserWithRoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUsersController {

    private final InternalUsersFacade facade;

    @PostMapping("/filter")
    public PagedModel<UserWithRoleResponse> filterUsersOfOrganization(
            @Valid
            @RequestBody
            UserWithRolesFilterRequest request
    ) {
        return facade.filterUsersOfOrganization(request);
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(
            @PathVariable("userId") Long userId
    ) {
        return facade.getUserById(userId);
    }

    @GetMapping
    public List<UserLightResponse> getUsersByIds(
            @RequestParam("userId") List<Long> usersIds
    ) {
        return facade.getUsersByIds(usersIds);
    }

}
