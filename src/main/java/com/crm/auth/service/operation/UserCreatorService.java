package com.crm.auth.service.operation;

import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.UserService;
import com.crm.auth.service.producer.SendPasswordToEmailProducer;
import com.crm.auth.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreatorService {

    private final UserService userService;

    private final SendPasswordToEmailProducer producer;

    @Value("${app.password.length}")
    private Integer passwordLength;

    public User createUser(SignUpRequest userRequest) {

        if (userRequest.getGeneratePassword()) {
            return createUserAndSendPassword(userRequest);
        }

        return userService.createUser(userRequest);
    }

    private User createUserAndSendPassword(SignUpRequest userRequest) {

        String generatedPassword = PasswordGenerator.generatePassword(passwordLength);

        userRequest.setPassword(generatedPassword);

        User user = userService.createUser(userRequest);

        producer.sendPassword(user.getEmail(), generatedPassword);

        return user;
    }

}
