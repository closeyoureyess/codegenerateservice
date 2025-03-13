package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription.TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT;

@Service
public class KafkaSenderServiceImpl implements KafkaSenderService {

    @Value("${kafka.stringTopicName}")
    private final String stringTopicName;

    @Value("${kafka.topic-name.booleanVerifyToken}")
    private final String booleanTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaSenderServiceImpl(String stringTopicName, String booleanTopicName,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        this.stringTopicName = stringTopicName;
        this.booleanTopicName = booleanTopicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendToTopic(String topic, Object message) throws KafkaSenderRuntimeException {
        topic = qualifyTopic(topic, message);
        kafkaTemplate.send(topic, message);
    }

    private String qualifyTopic(String topic, Object message) throws KafkaSenderRuntimeException {
        if (topic.equals(stringTopicName) && message instanceof String) {
            topic = stringTopicName;
        } else if (topic.equals( booleanTopicName) && message instanceof Boolean) {
            topic =  booleanTopicName;
        } else {
            throw new KafkaSenderRuntimeException(TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT.getDescription());
        }
        return topic;
    }
}
