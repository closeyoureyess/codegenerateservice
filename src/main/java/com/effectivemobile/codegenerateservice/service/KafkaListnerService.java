package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;

public interface KafkaListnerService {

    void listenEmail(OneTimeTokenDto oneTimeTokenDto);

    void listenToken(OneTimeTokenDto oneTimeTokenDto) throws TokenNotExistException;

}
