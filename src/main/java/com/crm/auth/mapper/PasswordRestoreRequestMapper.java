package com.crm.auth.mapper;

import com.crm.auth.dto.response.RestorePasswordResponse;
import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PasswordRestoreRequestMapper {

    RestorePasswordResponse toResponse(PasswordRestoreRequest entity);

}
