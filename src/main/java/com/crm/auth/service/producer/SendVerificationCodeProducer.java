package com.crm.auth.service.producer;

import com.crm.sharedlib.messaging.dto.amqp.SendVerificationCodeByEmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.crm.auth.constants.RabbitMQConstants.AUTH_TOPIC_EXCHANGE_NAME;
import static com.crm.auth.constants.RabbitMQConstants.USER_RESTORE_PASSWORD_ROUTING_KEY;


@Service
@RequiredArgsConstructor
public class SendVerificationCodeProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendVerificationCode(String email, Integer verificationCode) {
        SendVerificationCodeByEmailMessage message =
                new SendVerificationCodeByEmailMessage(email, verificationCode.toString());

        rabbitTemplate.convertAndSend(AUTH_TOPIC_EXCHANGE_NAME, USER_RESTORE_PASSWORD_ROUTING_KEY, message);
    }

}
