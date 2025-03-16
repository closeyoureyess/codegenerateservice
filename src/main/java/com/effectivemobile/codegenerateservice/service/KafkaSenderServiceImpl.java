package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
        topic = qualifyTopic(topic, message);
        kafkaTemplate.send(topic, message);
    }

    private String qualifyTopic(String topic, Object message) throws KafkaSenderRuntimeException {
        if (message instanceof OneTimeTokenDto) {
            if (topic.equals(objectTokenWasUsedTopicName)) {
                topic = objectTokenWasUsedTopicName;
            } else {
                throw new KafkaSenderRuntimeException(TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT.getDescription());
            }
        }
        return topic;
    }
}
