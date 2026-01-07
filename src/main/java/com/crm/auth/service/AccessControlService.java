package com.crm.auth.service;

import com.crm.auth.persistance.entity.AccessControl;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.repository.AccessControlRepository;
import com.crm.sharedlib.core.dto.request.ResourceWithActionsRequest;
import com.crm.sharedlib.core.enums.Action;
import com.crm.sharedlib.core.enums.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.crm.sharedlib.core.enums.Action.*;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final AccessControlRepository accessControlRepository;

    public List<AccessControl> createAccessControls(
            List<ResourceWithActionsRequest> request,
            Role role
    ) {

        Map<Resource, List<Action>> map = request.stream()
                .collect(Collectors.toMap(
                        ResourceWithActionsRequest::getResource,
                        ResourceWithActionsRequest::getActions
                ));

        return this.createAccessControls(map, role);
    }

    @Transactional
    public List<AccessControl> createAccessControls(
            Map<Resource, List<Action>> resourcesWithActions,
            Role role
    ) {

        ArrayList<AccessControl> accessControls =
                new ArrayList<>();

        for (Map.Entry<Resource, List<Action>> entry : resourcesWithActions.entrySet()) {

            replaceWithAllActionsIfAllCrudActionsSpecified(entry);

            for (Action action : entry.getValue()) {
                AccessControl control = new AccessControl();
                control.setAction(action);
                control.setResource(entry.getKey());
                control.setRole(role);
                accessControls.add(control);
            }

        }

        role.addAccessControls(accessControls);

        return accessControlRepository.saveAll(accessControls);
    }

    private void replaceWithAllActionsIfAllCrudActionsSpecified(
            Map.Entry<Resource, List<Action>> entry
    ) {
        Set<Action> actionSet = new HashSet<>(entry.getValue());
        EnumSet<Action> basicActions = EnumSet.of(CREATE, READ, UPDATE, DELETE);

        if (actionSet.containsAll(basicActions) && !actionSet.contains(ALL)) {
            entry.setValue(Collections.singletonList(ALL));
        }
    }

}
