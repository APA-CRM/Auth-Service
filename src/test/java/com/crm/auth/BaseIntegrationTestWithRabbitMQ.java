package com.crm.auth;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.amqp.core.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;

import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.ORGANIZATION_USER_ROLES_SYNC_QUEUE;

public abstract class BaseIntegrationTestWithRabbitMQ extends BaseIntegrationTest {

    private static RabbitMQContainer rabbitMQContainer;

    @BeforeAll
    public static void configureRabbit() {
        rabbitMQContainer = new RabbitMQContainer("rabbitmq:4.1-management");
        rabbitMQContainer.withExposedPorts(5672, 15672);

        rabbitMQContainer.start();

        System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitMQContainer.getAmqpPort().toString());
    }

    @AfterAll
    public static void shutdownRabbit() {
        rabbitMQContainer.close();
    }

    @TestConfiguration
    public static class RabbitConfiguration {

        @Bean
        public Queue syncRoles() {
            return QueueBuilder
                    .durable(ORGANIZATION_USER_ROLES_SYNC_QUEUE)
                    .build();
        }

        @Bean
        public Exchange usersEvents() {
            return ExchangeBuilder
                    .topicExchange("users.events")
                    .build();
        }

        @Bean
        public Binding userRolesChanged(
                Exchange usersEvents,
                Queue syncRoles
        ) {
            return BindingBuilder
                    .bind(syncRoles)
                    .to(usersEvents)
                    .with("users.roles-changed").noargs();
        }

    }

}
