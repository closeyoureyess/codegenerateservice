package com.effectivemobile.codegenerateservice.entity;

import jakarta.validation.constraints.Size;
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

    @Size(min = 36, max = 36, message = "UUID must be 36 characters")
    private String userToken;

    private String email;

    private LocalDateTime createdTime;

    private LocalDateTime expiredTime;

    private Boolean used;
}
