package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeToken;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListnerServiceImpl implements KafkaListnerService {

    private final OneTimeTokenService oneTimeTokenService;

    @Autowired
    public KafkaListnerServiceImpl(OneTimeTokenService oneTimeTokenService) {
        this.oneTimeTokenService = oneTimeTokenService;
    }

    @KafkaListener(topics = "${kafka.consumer.topic-name.object-email-address}", groupId = "codegen-service-group-one")
    @Override
    public void listenEmail(CustomUser customUser) {
        oneTimeTokenService.createToken(customUser);
    }

    @KafkaListener(topics = "${kafka.consumer.topic-name.object-token}", groupId = "codegen-service-group-two")
    @Override
    public void listenToken(OneTimeToken oneTimeToken) throws TokenNotExistException {
        oneTimeTokenService.verifyToken(oneTimeToken);
    }
}
