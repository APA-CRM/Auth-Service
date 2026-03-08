package com.crm.auth.config;

import com.crm.sharedlib.rbac.dto.UserPermission;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, UserPermission> redisTemplate(
            RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper
    ) {
        RedisTemplate<String, UserPermission> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setValueSerializer(valueSerializer);
        template.setKeySerializer(RedisSerializer.string());
        return template;
    }

}
