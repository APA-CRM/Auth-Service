package com.crm.auth.constants;

// TODO: Move all constants to the shared-lib and used them from there.
public class RabbitMQConstants {

    public final static String AUTH_TOPIC_EXCHANGE_NAME = "auth-service.event";

    public final static String RANDOM_PASSWORD_ROUTING_KEY = "user.random-password";

    public final static String USER_RESTORE_PASSWORD_ROUTING_KEY = "user.restore-password";

}
