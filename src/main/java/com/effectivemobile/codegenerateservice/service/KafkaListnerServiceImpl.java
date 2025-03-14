package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
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

    @KafkaListener(topics = "${kafka.topic-name.objectEmail}")
    @Override
    public void listenEmail(OneTimeTokenDto oneTimeTokenDto) {
        if (oneTimeTokenDto != null && oneTimeTokenDto.getEmail() != null) {
            String userEmail = oneTimeTokenDto.getEmail();
            oneTimeTokenService.createToken(userEmail);
        }
    }

    @KafkaListener(topics = "${kafka.topic-name.tokenObject}")
    @Override
    public void listenToken(OneTimeTokenDto oneTimeTokenDto) throws TokenNotExistException {
        if (oneTimeTokenDto != null && oneTimeTokenDto.getUserToken() != null) {
            String userToken = oneTimeTokenDto.getUserToken();
            oneTimeTokenService.verifyToken(userToken);
        }
    }
}
