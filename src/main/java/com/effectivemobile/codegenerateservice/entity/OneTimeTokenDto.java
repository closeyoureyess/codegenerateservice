package com.effectivemobile.codegenerateservice.entity;

import com.effectivemobile.codegenerateservice.others.kafkaconsumervalidation.ObjectEmailValidationGroup;
import com.effectivemobile.codegenerateservice.others.kafkaconsumervalidation.TokenObjectValidationGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class OneTimeTokenDto {

    @Null(message = "UserToken field must be null", groups = ObjectEmailValidationGroup.class)
    @NotNull(message = "UserToken field don't be null", groups = TokenObjectValidationGroup.class)
    private String userToken;

    @NotNull(message = "Email field don't be null", groups = ObjectEmailValidationGroup.class)
    @Null(message = "Email field must be null", groups = TokenObjectValidationGroup.class)
    private String email;

    @Null(message = "CreatedTime field must be null",
            groups = {ObjectEmailValidationGroup.class, TokenObjectValidationGroup.class})
    private LocalDateTime createdTime;

    @Null(message = "Expired time must be null",
            groups = {ObjectEmailValidationGroup.class, TokenObjectValidationGroup.class})
    private LocalDateTime expiredTime;

    @Null(message = "Used field must be null",
            groups = {ObjectEmailValidationGroup.class, TokenObjectValidationGroup.class})
    private Boolean used;
}
