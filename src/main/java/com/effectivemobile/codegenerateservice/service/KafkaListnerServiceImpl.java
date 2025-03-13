package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import lombok.NoArgsConstructor;
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

    @KafkaListener(topics = "${kafka.topic-name.stringEmail}")
    @Override
    public void listenEmail(String stringEmail) {
        if (stringEmail != null) {
            oneTimeTokenService.createToken(stringEmail);
        }
    }

    @KafkaListener(topics = "${kafka.topic-name.stringToken}")
    @Override
    public void listenToken(String stringToken) throws TokenNotExistException {
        if (stringToken != null) {
            oneTimeTokenService.verifyToken(stringToken);
        }
    }
}
