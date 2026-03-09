package com.crm.auth.config;

import com.crm.sharedlib.rbac.dto.UserPermission;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, UserPermission> redisTemplate(
            RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper
    ) {
        RedisTemplate<String, UserPermission> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<UserPermission> valueSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, UserPermission.class);

        template.setValueSerializer(valueSerializer);
        template.setKeySerializer(RedisSerializer.string());

        return template;
    }

}
