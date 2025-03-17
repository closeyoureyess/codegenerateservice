package com.effectivemobile.codegenerateservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=localhost:9092"
})
class KafkaProducerConfigTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProducerFactory<String, Object> producerFactory;

    @Test
    void producerFactoryConfigurations() {
        Map<String, Object> configs = producerFactory.getConfigurationProperties();

        Assertions.assertEquals("localhost:9092", configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        Assertions.assertEquals(StringSerializer.class, configs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        Assertions.assertTrue(configs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG).toString()
                .contains("JsonSerializer"));

        Assertions.assertTrue(configs.get(JsonSerializer.TYPE_MAPPINGS).toString()
                .contains("oneTimeToken:com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto"));
    }

    @Test
    void kafkaTemplateShouldBeConfigured() {
        Assertions.assertNotNull(kafkaTemplate);
        Assertions.assertSame(producerFactory, kafkaTemplate.getProducerFactory());
    }
}
