package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.AbstractContainerTest;
import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class KafkaSenderServiceImplTest extends AbstractContainerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaSenderServiceImpl kafkaSenderService;

    private final String validTopic = "validate-entered-token";
    private final String invalidTopic = "invalid-topic";

    @BeforeEach
    void setUp() {
        kafkaSenderService = new KafkaSenderServiceImpl(kafkaTemplate);
        kafkaSenderService.setObjectTokenWasUsedTopicName(validTopic); // Устанавливаем валидный топик
    }

    @Test
    void sendToTopic_WithValidTopic_ShouldSendMessage() {
        OneTimeTokenDto tokenDto = new OneTimeTokenDto();
        Assertions.assertDoesNotThrow(() -> kafkaSenderService.sendToTopic(validTopic, tokenDto));
        Mockito.verify(kafkaTemplate, times(1)).send(any(Message.class));
    }

    @Test
    void sendToTopic_WithInvalidTopic_ShouldThrowException() {
        Object message = new Object();

        KafkaSenderRuntimeException exception = assertThrows(
                KafkaSenderRuntimeException.class,
                () -> kafkaSenderService.sendToTopic(invalidTopic, message)
        );
        Assertions.assertEquals(
                "Переданное наименование топика некорректно, либо, с переданным топиком передан несовместимый объект",
                exception.getMessage()
        );
    }

    @Test
    void sendToTopic_ShouldSetCorrectHeaders() {
        OneTimeTokenDto tokenDto = new OneTimeTokenDto();
        kafkaSenderService.sendToTopic(validTopic, tokenDto);
        Mockito.verify(kafkaTemplate).send(any(Message.class));
    }
}