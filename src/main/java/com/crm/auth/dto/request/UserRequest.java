package com.crm.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String aboutYourself;
}
