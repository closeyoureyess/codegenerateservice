package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeToken;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;

public interface KafkaListnerService {

    void listenEmail(CustomUser customUser);

    void listenToken(OneTimeToken oneTimeToken) throws TokenNotExistException;

}
