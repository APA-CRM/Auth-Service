package com.crm.auth.controller;

import com.crm.auth.facade.AccessControlFacade;
import com.crm.sharedlib.enums.Action;
import com.crm.sharedlib.enums.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/access-control")
@RequiredArgsConstructor
public class AccessControlController {

    private final AccessControlFacade facade;

    @GetMapping("/resources")
    public List<Resource> getAllResources() {
        return facade.getAllResources();
    }

    @GetMapping("/actions")
    public List<Action> getAllActions() {
        return facade.getAllActions();
    }

}
