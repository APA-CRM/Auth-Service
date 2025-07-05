package com.crm.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank(message = "First name can't be blank")
    private String firstName;

    @NotBlank(message = "Last name can't be blank")
    private String lastName;

    @Pattern(
            regexp = "^\\+?\\d{1,3}?[- .]?\\(?\\d{2,4}\\)?[- .]?\\d{3}[- .]?\\d{2}[- .]?\\d{2}$",
            message = "Phone number must follow the number pattern"
    )
    private String phoneNumber;

    @Size(max = 360, message = "Max size of about yourself 360 characters")
    private String aboutYourself;
}
