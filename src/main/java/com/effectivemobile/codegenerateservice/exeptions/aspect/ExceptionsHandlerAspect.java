package com.effectivemobile.codegenerateservice.exeptions.aspect;

import com.effectivemobile.codegenerateservice.exeptions.KafkaSenderRuntimeException;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription.TOKEN_NOT_FOUND;
import static com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription.TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT;

@Aspect
@Component
@Slf4j
public class ExceptionsHandlerAspect {

    @Pointcut("execution(* com.effectivemobile.codegenerateservice.services.*.*(..))")
    public void pontCutServiceMethods() {}

    @AfterThrowing(pointcut = "pontCutServiceMethods()", throwing = "tokenNotExistException")
    public void handleTokenNotExistException(TokenNotExistException tokenNotExistException) {
        log.error("{}\\n{}\\n{}", TOKEN_NOT_FOUND.getDescription(), tokenNotExistException.getClass(),
                tokenNotExistException.getMessage(), tokenNotExistException);
    }

    @AfterThrowing(pointcut = "pontCutServiceMethods()", throwing = "kafkaSenderRuntimeException")
    public void handleTokenNotExistException(KafkaSenderRuntimeException kafkaSenderRuntimeException) {
        log.error("{}\\n{}\\n{}", TOPIC_OR_OBJECT_IN_KAFKA_IS_INCORRECT.getDescription(), kafkaSenderRuntimeException.getClass(),
                kafkaSenderRuntimeException.getMessage(), kafkaSenderRuntimeException);
    }

    @AfterThrowing(pointcut = "pontCutServiceMethods()", throwing = "exception")
    public void handleException(Exception exception) {
        log.error("{}\\n{}\\n{}\\n{}", "Error", exception.getClass(), exception.getSuppressed(),
                exception.getMessage(), exception);
    }

    @AfterThrowing(pointcut = "pontCutServiceMethods()", throwing = "runTimeException")
    public void handleRuntimeException(RuntimeException runTimeException) {
        log.error("{}\\n{}\\n{}\\n{}", "Error", runTimeException.getClass(), runTimeException.getSuppressed(),
                runTimeException.getMessage(), runTimeException);
    }
}