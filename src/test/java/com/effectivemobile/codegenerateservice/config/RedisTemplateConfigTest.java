package com.effectivemobile.codegenerateservice.config;

import com.effectivemobile.codegenerateservice.AbstractContainerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootTest
class RedisTemplateConfigTest extends AbstractContainerTest {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    @Qualifier("redisConfig")
    private ObjectMapper objectMapper;

    @Test
    void redisTemplateSerializers() {
        Assertions.assertInstanceOf(StringRedisSerializer.class, redisTemplate.getKeySerializer());
        Assertions.assertInstanceOf(GenericJackson2JsonRedisSerializer.class, redisTemplate.getValueSerializer());
    }

    @Test
    void objectMapperConfiguration() {
        Assertions.assertFalse(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        Assertions.assertNotNull(objectMapper.getRegisteredModuleIds().contains(JavaTimeModule.class.getName()));
    }
}
