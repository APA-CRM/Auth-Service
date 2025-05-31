package com.crm.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignInRequest {

    @NotBlank(message = "Login can't be blank")
    private String login;

    @NotBlank(message = "Password can't be blank")
    private String password;

}
