package com.effectivemobile.codegenerateservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.consumer.bootstrap-servers=localhost:9092"
})
class KafkaConsumerConfigTest {

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory;

    @Test
    void consumerFactoryConfigurations() {
        Map<String, Object> configs = consumerFactory.getConfigurationProperties();

        Assertions.assertEquals("localhost:9092", configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        Assertions.assertEquals(ErrorHandlingDeserializer.class, configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        Assertions.assertTrue(configs.get(JsonDeserializer.TRUSTED_PACKAGES).toString().contains("com.effectivemobile.codegenerateservice.entity"));
    }

    @Test
    void containerFactoryShouldBeConfigured() {
        Assertions.assertNotNull(containerFactory);
        Assertions.assertNotNull(containerFactory.getConsumerFactory()); // Проверяем, что фабрика потребителей настроена
    }
}
