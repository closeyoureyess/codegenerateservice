package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface KafkaSenderService {

    void sendToTopic(@Valid @NotBlank String topic, @NotNull Object message) throws KafkaSenderRuntimeException;

}
