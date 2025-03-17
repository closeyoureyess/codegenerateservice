package com.effectivemobile.codegenerateservice.exeptions;

import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class InfoMessagesHandler {
    @Before("@annotation(kafkaListener)")
    public void logBefore(JoinPoint joinPoint, KafkaListener kafkaListener) {
        Object[] args = joinPoint.getArgs();
        log.info("📩 Получено сообщение из Kafka: Topic = {}, Аргументы = {}",
                kafkaListener.topics(), args);
    }

    @Around("execution(* com.effectivemobile.codegenerateservice.service.OneTimeTokenServiceImpl.createToken(..))")
    public Object logReturnValue(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        OneTimeTokenDto oneTimeTokenDto = (OneTimeTokenDto) result;
        log.info("📩 Название = {}\\n📩Cгенерирован токен = {}", joinPoint.getSignature().getName(), oneTimeTokenDto.getUserToken());

        return result;
    }
}