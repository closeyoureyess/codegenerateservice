package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;

public interface KafkaSenderService {

    void sendToTopic(String topic, Object message) throws KafkaSenderRuntimeException;

}
