package com.crm.auth.mapper;

import com.crm.auth.persistance.entity.AccessControl;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.entity.redis.ResourcePermission;
import com.crm.sharedlib.enums.Action;
import com.crm.sharedlib.enums.Resource;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.crm.sharedlib.enums.Action.*;

@Component
public class AccessControlMapper {

    private final EnumSet<Action> BASIC_ACTIONS = EnumSet.of(CREATE, READ, UPDATE, DELETE);

    public List<ResourcePermission> toResourcePermission(List<Role> roles) {

        List<ResourcePermission> list = new ArrayList<>();

        Map<Resource, Set<Action>> groupedActions = new HashMap<>();

        for (Role role : roles) {
            fillGroupedResources(role, groupedActions);
        }

        if (groupedActions.containsKey(Resource.ALL)) {
            ResourcePermission resource = createResource(Resource.ALL, groupedActions.get(Resource.ALL));

            list.add(resource);
        } else {
            for (Map.Entry<Resource, Set<Action>> entry : groupedActions.entrySet()) {
                ResourcePermission resource = createResource(entry.getKey(), entry.getValue());

                list.add(resource);
            }
        }

        return list;
    }

    private void fillGroupedResources(Role role, Map<Resource, Set<Action>> groupedActions) {
        for (AccessControl accessControl : role.getAccessControls()) {
            groupedActions
                    .computeIfAbsent(accessControl.getResource(), k -> new HashSet<>())
                    .add(accessControl.getAction());
        }
    }

    private ResourcePermission createResource(Resource resource, Set<Action> actions) {

        List<Action> checkedActions = checkIfAllBasicActionPresent(actions)
                .stream().toList();

        ResourcePermission response = new ResourcePermission();
        response.setResource(resource);
        response.setActions(checkedActions);

        return response;
    }

    private Set<Action> checkIfAllBasicActionPresent(Set<Action> actions) {
        return actions.containsAll(BASIC_ACTIONS) ? Set.of(ALL) : actions;
    }
}
