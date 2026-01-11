package com.crm.auth.service.producer;

import com.crm.sharedlib.messaging.dto.amqp.SendVerificationCodeByEmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.SEND_VERIFICATION_CODE_QUEUE;

@Service
@RequiredArgsConstructor
public class SendVerificationCodeProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendVerificationCode(String email, Integer verificationCode) {
        SendVerificationCodeByEmailMessage message =
                new SendVerificationCodeByEmailMessage(email, verificationCode.toString());

        rabbitTemplate.convertAndSend(SEND_VERIFICATION_CODE_QUEUE, message);
    }

}
