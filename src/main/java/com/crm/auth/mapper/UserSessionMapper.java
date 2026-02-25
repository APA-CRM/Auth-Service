package com.crm.auth.mapper;

import com.crm.auth.dto.response.UserSessionDto;
import com.crm.auth.persistance.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSessionMapper {

    UserSessionDto toDto(RefreshToken refreshToken);

}
