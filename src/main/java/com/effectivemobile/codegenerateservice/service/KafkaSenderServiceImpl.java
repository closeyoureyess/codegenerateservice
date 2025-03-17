package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.effectivemobile.codegenerateservice.constants.Constants.KAFKA_PRODUCER_TRUST_ONETIMETOKEN;
import static com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription.TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT;

@Service
public class KafkaSenderServiceImpl implements KafkaSenderService {

    @Value("${kafka.producer.topic-name.token-is-valid}")
    private String objectTokenWasUsedTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaSenderServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendToTopic(String topic, Object message) throws KafkaSenderRuntimeException {
        String key;
        if (topic.equals(objectTokenWasUsedTopicName)) {
            topic = objectTokenWasUsedTopicName;
            key = KAFKA_PRODUCER_TRUST_ONETIMETOKEN;
        } else {
            throw new KafkaSenderRuntimeException(TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT.getDescription());
        }
        Message<Object> kafkaMessage = MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(JsonSerializer.TYPE_MAPPINGS, key)
                .build();
        kafkaTemplate.send(kafkaMessage);
    }
}
