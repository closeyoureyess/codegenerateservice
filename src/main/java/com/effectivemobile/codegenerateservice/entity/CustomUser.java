package com.effectivemobile.codegenerateservice.entity;

import com.effectivemobile.codegenerateservice.others.kafkaconsumervalidation.ObjectEmailValidationGroup;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class CustomUser {

    @Email(message = "Email don't be incorrect", groups = ObjectEmailValidationGroup.class)
    private String email;

}
