package com.effectivemobile.codegenerateservice.config;

import com.effectivemobile.codegenerateservice.AbstractContainerTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DefaultClassesConfigTest extends AbstractContainerTest {

    @Autowired
    @Qualifier("redisConfig")
    private ObjectMapper objectMapper;

    @Test
    void objectMapperShouldHaveJavaTimeModule() throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        String json = objectMapper.writeValueAsString(now);
        assertFalse(json.matches("\\[\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+\\]"));
        assertTrue(json.contains("T"));
    }

    @Test
    void objectMapperShouldDisableTimestamps() {
        Assertions.assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }
}
