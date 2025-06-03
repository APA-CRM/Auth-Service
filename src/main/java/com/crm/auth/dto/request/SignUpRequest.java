package com.crm.auth.dto.request;

import com.crm.auth.validation.SignUpConstraint;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SignUpConstraint(message = "Password is empty")
public class SignUpRequest {

    @NotBlank(message = "Login can't be blank")
    @Size(min = 5, message = "Login must have at least 5 characters")
    private String login;

    @Email(message = "Email must follow email pattern")
    @NotNull(message = "Email is required")
    private String email;

    private String firstName;

    private String lastName;

    @NotNull(message = "You need to choice password policy")
    private Boolean generatePassword;

    @Nullable
    private String password;

}
