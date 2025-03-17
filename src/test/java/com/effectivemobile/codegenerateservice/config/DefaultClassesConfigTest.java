package com.effectivemobile.codegenerateservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultClassesConfigTest {

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
