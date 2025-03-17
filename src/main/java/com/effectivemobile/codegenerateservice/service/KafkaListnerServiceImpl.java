package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaListnerServiceImpl implements KafkaListnerService {

    private final OneTimeTokenService oneTimeTokenService;

    @Autowired
    public KafkaListnerServiceImpl(OneTimeTokenService oneTimeTokenService) {
        this.oneTimeTokenService = oneTimeTokenService;
    }

    @KafkaListener(topics = "${kafka.consumer.topic-name.object-email-address}", groupId = "codegen-service-group-one")
    @Override
    public void listenEmail(CustomUser customUser) {
        log.info("New user registered: {}", customUser.getEmail());
        oneTimeTokenService.createToken(customUser);
    }

    @KafkaListener(topics = "${kafka.consumer.topic-name.object-token}", groupId = "codegen-service-group-two")
    @Override
    public void listenToken(OneTimeTokenDto oneTimeTokenDto) throws TokenNotExistException {
        oneTimeTokenService.verifyToken(oneTimeTokenDto);
    }

    /*@KafkaListener(topics = "${kafka.consumer.topic-name.object-token}", groupId = "codegen-service-group-two")
    @Override
    public void listenToken(*//*@Payload Object payload, @Header(KafkaHeaders.RECEIVED_KEY) String key,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                            @Header(KafkaHeaders.OFFSET) long offset,
                            @Headers Map<String, Object> headers*//*) {
        log.info("Ключ: {}", key);
        log.info("Заголовки Kafka: {}", headers);
        log.info("Получено сообщение: {}", payload);
    }*/
}
