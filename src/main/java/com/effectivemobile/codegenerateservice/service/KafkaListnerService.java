package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;

public interface KafkaListnerService {

    void listenEmail(String stringEmail);

    void listenToken(String stringToken) throws TokenNotExistException;

}
