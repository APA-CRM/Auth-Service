package com.crm.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotNull(message = "Refresh token can't be null")
    private String refreshToken;

}
