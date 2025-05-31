package com.crm.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshJwtTokenRequest {

    @NotNull(message = "Refresh token can't be null")
    private String refreshToken;

}
