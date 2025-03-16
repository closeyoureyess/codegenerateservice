package com.effectivemobile.codegenerateservice.config;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private final String bootStrapServers;

    @Autowired
    public KafkaProducerConfig(@Value("${spring.kafka.producer.bootstrap-servers}") String bootStrapServers) {
        this.bootStrapServers = bootStrapServers;
    }

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        JsonSerializer<Object> serializer = new JsonSerializer<>();
        serializer.setTypeMapper(new DefaultJackson2JavaTypeMapper() {
            {
                setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
                addTrustedPackages(
                        "com.effectivemobile.authservice.entity",
                        "com.effectivemobile.codegenerateservice.entity"
                );
                setIdClassMapping(Map.of(
                        "customUser", CustomUser.class,
                        "oneTimeToken", OneTimeTokenDto.class
                ));
            }
        });

        return new DefaultKafkaProducerFactory<>(
                producerConfigs(),
                new StringSerializer(),
                serializer
        );
        /*return new DefaultKafkaProducerFactory<>(producerConfigs());*/
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
