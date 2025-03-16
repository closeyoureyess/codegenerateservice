package com.effectivemobile.codegenerateservice.exeptions;

import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class InfoMessagesHandler {
    @AfterReturning("execution(* com.example.service.MyService.specificMethod(..))")
    public void beforeServiceMethod(OneTimeTokenDto oneTimeTokenDto) {
        log.info("{}: {}\\n{}: {}\\n{}: {}\\n{}: {}\\n{}: {}", InfoDescriptions.IS_GENERATED_TOKEN, oneTimeTokenDto.getUserToken(),
                InfoDescriptions.IS_USER_EMAIL, oneTimeTokenDto.getEmail(),
                InfoDescriptions.TOKEN_CREATED_TIME, oneTimeTokenDto.getCreatedTime(),
                InfoDescriptions.TOKEN_EXPIRE_TIME, oneTimeTokenDto.getExpiredTime(),
                InfoDescriptions.IS_USED, oneTimeTokenDto.getUsed());
    }
}
