package com.crm.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestorePasswordResponse {

    private UUID id;

    private Integer attemptsCount;

    private Instant createAt;

    private Instant updateAt;

}
