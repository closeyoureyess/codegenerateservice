package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeToken;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import com.effectivemobile.codegenerateservice.others.kafkaconsumervalidation.ObjectEmailValidationGroup;
import com.effectivemobile.codegenerateservice.others.kafkaconsumervalidation.TokenObjectValidationGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

public interface OneTimeTokenService {

    @Validated(ObjectEmailValidationGroup.class)
    OneTimeToken createToken(@Valid @NotNull(message = "The object cannot be null.")
                             CustomUser customUser);

    @Validated(TokenObjectValidationGroup.class)
    void verifyToken(@Valid @NotNull(message = "The object cannot be null.") OneTimeToken oneTimeToken) throws TokenNotExistException;
}
