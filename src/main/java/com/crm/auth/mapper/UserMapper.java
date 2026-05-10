package com.crm.auth.mapper;

import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.dto.request.UserUpdateRequest;
import com.crm.auth.dto.response.UserLightResponse;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.entity.User;
import com.crm.sharedlib.core.dto.response.RoleLightResponse;
import com.crm.sharedlib.core.dto.response.UserResponse;
import com.crm.sharedlib.core.dto.response.UserWithRoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.util.Objects.isNull;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {

    @Autowired
    protected RoleMapper roleMapper;

    public abstract UserResponse toDto(User user);

    @Mapping(expression = "java(getLightRoles(roles))", target = "roles")
    public abstract UserWithRoleResponse toUserWithRoleResponse(User user, @Nullable List<Role> roles);

    public abstract UserLightResponse toLightResponse(User user);

    @Mapping(expression = "java(trimName(request.getFirstName()))", target = "firstName")
    @Mapping(expression = "java(trimName(request.getLastName()))", target = "lastName")
    @Mapping(expression = "java(buildFullName(request.getFirstName(),request.getLastName()))", target = "fullName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    public abstract User toEntity(SignUpRequest request);

    @Mapping(expression = "java(trimName(request.getFirstName()))", target = "firstName")
    @Mapping(expression = "java(trimName(request.getLastName()))", target = "lastName")
    @Mapping(expression = "java(buildFullName(request.getFirstName(),request.getLastName()))", target = "fullName")
    public abstract User updateUserFromRequest(UserUpdateRequest request,
                                               @MappingTarget User user);

    protected String trimName(String name) {
        if (StringUtils.hasText(name)) {
            return name.trim();
        }

        return null;
    }

    protected String buildFullName(String firstName, String lastName) {
        return (firstName.trim() + " " + lastName.trim());
    }

    protected List<RoleLightResponse> getLightRoles(List<Role> roles) {
        if (isNull(roles)) {
            return null;
        }

        return roles.stream()
                .map(roleMapper::toLightDto)
                .toList();
    }

}
