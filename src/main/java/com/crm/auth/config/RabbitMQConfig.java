package com.crm.auth.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.crm.auth.constants.RabbitMQConstants.AUTH_TOPIC_EXCHANGE_NAME;
import static com.crm.auth.constants.RabbitMQConstants.RANDOM_PASSWORD_BINDING_NAME;
import static com.crm.sharedlib.consts.CrmConstants.ORGANIZATION_USER_ROLES_SYNC_QUEUE;
import static com.crm.sharedlib.consts.CrmConstants.SEND_PASSWORD_QUEUE;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private Integer port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);

        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        return factory;
    }

    @Bean
    public AmqpTemplate amqpTemplate(
            MessageConverter messageConverter,
            ConnectionFactory connectionFactory
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setConnectionFactory(connectionFactory);

        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Configuration
    public static class ProducerConfig {

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

}
