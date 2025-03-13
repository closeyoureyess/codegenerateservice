package com.effectivemobile.codegenerateservice.exeptions;

import lombok.Getter;

@Getter
public enum ExceptionsDescription {

    TOKEN_NOT_FOUND("Не найден одноразовый код для входа. Запросите токен повторно, " +
            "повторите попытку входа еще раз."),

    TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT("Переданное наименование топика некорректно, либо, с переданным топиком передан несовместимый объект");

    private String description;

    ExceptionsDescription(String description) {
        this.description = description;
    }
}
