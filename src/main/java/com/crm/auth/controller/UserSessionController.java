package com.crm.auth.controller;

import com.crm.auth.dto.response.UserSessionDto;
import com.crm.auth.facade.UserSessionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.crm.sharedlib.core.consts.CrmHeaders.USER_ID_HEADER_NAME;

@RestController
@RequestMapping("/api/users/sessions")
@RequiredArgsConstructor
public class UserSessionController {

    private final UserSessionFacade facade;

    @GetMapping
    public List<UserSessionDto> getUserSessions(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId
    ) {
        return facade.getUserSessions(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void endUserSession(
            @RequestHeader(USER_ID_HEADER_NAME) Long authUserId,
            @PathVariable("id") UUID sessionId
    ) {
        facade.endUserSession(sessionId, authUserId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void endAllUsersSessions(
            @RequestHeader(USER_ID_HEADER_NAME) Long userId
    ) {
        facade.endAllUsersSessions(userId);
    }

}
