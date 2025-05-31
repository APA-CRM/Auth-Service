package com.crm.auth.facade;

import com.crm.sharedlib.annotations.Facade;
import com.crm.sharedlib.enums.Action;
import com.crm.sharedlib.enums.Resource;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Facade
@RequiredArgsConstructor
public class AccessControlFacade {

    public List<Resource> getAllResources() {
        return List.of(Resource.values());
    }

    public List<Action> getAllActions() {
        return List.of(Action.values());
    }

}
