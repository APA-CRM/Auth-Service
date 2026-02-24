package com.crm.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDto {

    private UUID id;

    private String deviceInfo;

    private Instant createdAt;

    private Instant updatedAt;

}

