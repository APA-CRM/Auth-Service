package com.crm.auth.mapper;

import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.persistance.entity.Role;
import com.crm.auth.persistance.entity.User;
import com.crm.sharedlib.dto.response.RoleLightResponse;
import com.crm.sharedlib.dto.response.UserResponse;
import com.crm.sharedlib.dto.response.UserWithRoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {

    @Autowired
    protected RoleMapper roleMapper;

    @Mapping(expression = "java(getFullNameOfUser(user))", target = "fullName")
    public abstract UserResponse toDto(User user);

    @Mapping(expression = "java(getLightRoles(roles))", target = "roles")
    @Mapping(expression = "java(getFullNameOfUser(user))", target = "fullName")
    public abstract UserWithRoleResponse toUserWithRoleResponse(User user, @Nullable List<Role> roles);

    @Mapping(expression = "java(trimName(request.getFirstName()))", target = "firstName")
    @Mapping(expression = "java(trimName(request.getLastName()))", target = "lastName")
    @Mapping(expression = "java(buildFullName(request.getFirstName(),request.getLastName()))", target = "fullName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    public abstract User toEntity(SignUpRequest request);


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

    protected String getFullNameOfUser(User user) {
        if (isNull(user.getFirstName()) && isNull(user.getLastName())) {
            return null;
        }

        if (nonNull(user.getFirstName()) && isNull(user.getLastName())) {
            return user.getFirstName();
        } else if (isNull(user.getFirstName())) {
            return user.getLastName();
        } else {
            return user.getFullName();
        }
    }

}
