package com.crm.auth.service.producer;

import com.crm.sharedlib.core.dto.amqp.SendPasswordByEmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.crm.auth.constants.RabbitMQConstants.AUTH_TOPIC_EXCHANGE_NAME;
import static com.crm.auth.constants.RabbitMQConstants.RANDOM_PASSWORD_BINDING_NAME;

@Service
@RequiredArgsConstructor
public class SendPasswordToEmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendPassword(String email, String password) {
        SendPasswordByEmailEvent message = new SendPasswordByEmailEvent(email, password);

        rabbitTemplate.convertAndSend(AUTH_TOPIC_EXCHANGE_NAME, RANDOM_PASSWORD_BINDING_NAME, message);
    }

}
