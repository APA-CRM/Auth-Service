package com.crm.auth.config;

import com.crm.sharedlib.messaging.config.BaseRabbitMQConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.crm.auth.constants.RabbitMQConstants.AUTH_TOPIC_EXCHANGE_NAME;
import static com.crm.auth.constants.RabbitMQConstants.RANDOM_PASSWORD_BINDING_NAME;
import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.ORGANIZATION_USER_ROLES_SYNC_QUEUE;
import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.SEND_PASSWORD_QUEUE;

@Configuration
@EnableRabbit
public class RabbitMQConfig extends BaseRabbitMQConfig {

    @Value("${app.ampq.organisation-user-sync-roles.queue.ttl}")
    private Integer orgUserSyncRolesQueueTtl;

    @Bean("sendPasswordQueue")
    public Queue sendPasswordQueue() {
        return QueueBuilder
                .durable(SEND_PASSWORD_QUEUE)
                .build();
    }

    @Bean("authEventsExchange")
    public TopicExchange authEventsExchange() {
        return ExchangeBuilder
                .topicExchange(AUTH_TOPIC_EXCHANGE_NAME)
                .build();
    }

    @Bean
    public Queue orgUserSyncRoles() {
        return QueueBuilder
                .durable(ORGANIZATION_USER_ROLES_SYNC_QUEUE)
                .ttl(orgUserSyncRolesQueueTtl)
                .build();
    }

    @Bean
    public Binding sendPasswordBinding(
            Queue sendPasswordQueue, TopicExchange authEventsExchange
    ) {
        return BindingBuilder.bind(sendPasswordQueue)
                .to(authEventsExchange)
                .with(RANDOM_PASSWORD_BINDING_NAME);
    }


}
