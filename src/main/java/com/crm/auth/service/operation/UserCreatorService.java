package com.crm.auth.service.operation;

import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.UserService;
import com.crm.auth.utils.PasswordGenerator;
import com.crm.sharedlib.dto.amqp.SendPasswordEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.crm.auth.constants.RabbitMQConstants.AUTH_TOPIC_EXCHANGE_NAME;
import static com.crm.auth.constants.RabbitMQConstants.RANDOM_PASSWORD_BINDING_NAME;

@Service
@RequiredArgsConstructor
public class UserCreatorService {

    private final UserService userService;

    private final RabbitTemplate rabbitTemplate;

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

        sendPasswordToQueue(user.getEmail(), generatedPassword);

        return user;
    }

    private void sendPasswordToQueue(String email, String password) {
        SendPasswordEmail message = new SendPasswordEmail(email, password);

        rabbitTemplate.convertAndSend(AUTH_TOPIC_EXCHANGE_NAME, RANDOM_PASSWORD_BINDING_NAME, message);
    }

}
