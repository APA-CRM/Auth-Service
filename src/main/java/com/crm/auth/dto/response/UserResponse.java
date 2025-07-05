package com.crm.auth.dto.response;

import com.crm.auth.persistance.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String aboutYourself;

    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();

        dto.setAboutYourself(user.getAboutYourself());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());

        return dto;
    }
}
