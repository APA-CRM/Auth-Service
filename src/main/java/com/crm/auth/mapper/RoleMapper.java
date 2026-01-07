package com.crm.auth.mapper;

import com.crm.auth.persistance.entity.AccessControl;
import com.crm.auth.persistance.entity.Role;
import com.crm.sharedlib.core.dto.response.AccessControlResponse;
import com.crm.sharedlib.core.dto.response.RoleLightResponse;
import com.crm.sharedlib.core.dto.response.RoleResponse;
import com.crm.sharedlib.core.enums.Action;
import com.crm.sharedlib.core.enums.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class RoleMapper {

    @Mapping(expression = "java(getAccessControls(role))", target = "accessControls")
    public abstract RoleResponse toDto(Role role);

    public abstract RoleLightResponse toLightDto(Role role);

    protected List<AccessControlResponse> getAccessControls(Role role) {
        Map<Resource, Set<Action>> groupedActions = new HashMap<>();

        for (AccessControl accessControl : role.getAccessControls()) {
            groupedActions
                    .computeIfAbsent(accessControl.getResource(), k -> new HashSet<>())
                    .add(accessControl.getAction());
        }

        List<AccessControlResponse> list = new ArrayList<>(groupedActions.size());

        for (Map.Entry<Resource, Set<Action>> entry : groupedActions.entrySet()) {
            AccessControlResponse response = new AccessControlResponse();
            response.setResource(entry.getKey());
            response.setActions(new ArrayList<>(entry.getValue()));
            list.add(response);
        }

        return list;
    }

}
