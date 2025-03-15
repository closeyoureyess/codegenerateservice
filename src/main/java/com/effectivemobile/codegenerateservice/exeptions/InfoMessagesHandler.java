package com.effectivemobile.codegenerateservice.exeptions;

import com.effectivemobile.codegenerateservice.entity.OneTimeToken;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class InfoMessagesHandler {
    @AfterReturning("execution(* com.example.service.MyService.specificMethod(..))")
    public void beforeServiceMethod(OneTimeToken oneTimeToken) {
        log.info("{}: {}\\n{}: {}\\n{}: {}\\n{}: {}\\n{}: {}", InfoDescriptions.IS_GENERATED_TOKEN, oneTimeToken.getUserToken(),
                InfoDescriptions.IS_USER_EMAIL, oneTimeToken.getEmail(),
                InfoDescriptions.TOKEN_CREATED_TIME, oneTimeToken.getCreatedTime(),
                InfoDescriptions.TOKEN_EXPIRE_TIME, oneTimeToken.getExpiredTime(),
                InfoDescriptions.IS_USED, oneTimeToken.getUsed());
    }
}
