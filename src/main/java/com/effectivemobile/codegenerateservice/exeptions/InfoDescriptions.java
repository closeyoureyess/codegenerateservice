package com.effectivemobile.codegenerateservice.exeptions;

import lombok.Getter;

@Getter
public enum InfoDescriptions {

    IS_GENERATED_TOKEN("Сгенерированный токен"),

    IS_USER_EMAIL("Емейл пользователя"),

    TOKEN_CREATED_TIME("Время создания токена"),

    TOKEN_EXPIRE_TIME("Время жизни токена"),

    IS_USED("Состоние токена (использован или нет)");

    private String description;

    InfoDescriptions(String description) {
        this.description = description;
    }
}
