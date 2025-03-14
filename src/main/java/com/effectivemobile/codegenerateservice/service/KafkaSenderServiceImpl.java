package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription.TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT;

@Service
public class KafkaSenderServiceImpl implements KafkaSenderService {

    @Value("${kafka.topic-name.tokenObject}")
    private final String tokenObjectTopicName;

    @Value("${kafka.topic-name.objectTokenWasUsed}")
    private final String objectTokenWasUsedTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaSenderServiceImpl(String tokenObjectTopicName, String objectTokenWasUsedTopicName,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.tokenObjectTopicName = tokenObjectTopicName;
        this.objectTokenWasUsedTopicName = objectTokenWasUsedTopicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendToTopic(String topic, Object message) throws KafkaSenderRuntimeException {
        topic = qualifyTopic(topic, message);
        kafkaTemplate.send(topic, message);
    }

    private String qualifyTopic(String topic, Object message) throws KafkaSenderRuntimeException {
        if (topic.equals(tokenObjectTopicName) && message instanceof String) {
            topic = tokenObjectTopicName;
        } else if (topic.equals(objectTokenWasUsedTopicName) && message instanceof Boolean) {
            topic = objectTokenWasUsedTopicName;
        } else {
            throw new KafkaSenderRuntimeException(TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT.getDescription());
        }
        return topic;
    }
}
