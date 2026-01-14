package com.crm.auth.dto.response;

import java.time.Instant;
import java.util.UUID;

public class RestorePasswordResponse {

    private UUID id;

    private Integer attemptsCount;

    private Instant createdAt;

    private Instant updateAt;

}
