package com.crm.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestorePasswordRequest {

    @Email(message = "Email must follow email pattern")
    @NotNull(message = "Email is required")
    private String email;

}
