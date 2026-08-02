package com.crm.auth;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@Slf4j
public class BaseIntegrationTestWithRedis extends BaseIntegrationTest {

    private static RedisContainer redisContainer;

    @BeforeAll
    public static void initRedisContainer() {
        int exposedPort = 6379;

        redisContainer = new RedisContainer("redis:8.4.0-alpine");
        redisContainer.withExposedPorts(exposedPort);
        redisContainer.withLogConsumer(new Slf4jLogConsumer(log));
        redisContainer.start();

        System.setProperty("spring.data.redis.host", redisContainer.getHost());
        System.setProperty("spring.data.redis.port", redisContainer.getFirstMappedPort().toString());
    }

    @AfterAll
    public static void stopRedisContainer() {
        redisContainer.stop();
    }

}
