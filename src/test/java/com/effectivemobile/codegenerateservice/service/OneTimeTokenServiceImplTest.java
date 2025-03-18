package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.AbstractContainerTest;
import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.effectivemobile.codegenerateservice.constants.Constants.EXPIRE_TIME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class OneTimeTokenServiceImplTest extends AbstractContainerTest {

    @Mock
    private RedisTemplate<Object, Object> redisTemplate;

    @Mock
    private ValueOperations<Object, Object> valueOperations;

    @Mock
    private KafkaSenderService kafkaSenderService;

    @Mock
    private ObjectMapper objectMapper;

    private OneTimeTokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        tokenService = new OneTimeTokenServiceImpl(redisTemplate, kafkaSenderService, objectMapper);
    }

    @Test
    void createToken_ShouldSaveTokenToRedis() {
        CustomUser user = new CustomUser();
        user.setEmail("test@example.com");

        OneTimeTokenDto result = tokenService.createToken(user);

        Assertions.assertNotNull(result.getUserToken());
        Assertions.assertEquals(user.getEmail(), result.getEmail());
        Mockito.verify(valueOperations).set(
                eq(result.getUserToken()),
                eq(result),
                eq(EXPIRE_TIME),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    void verifyToken_WhenTokenExists_ShouldMarkAsUsedAndSendToKafka() throws TokenNotExistException {
        String token = UUID.randomUUID().toString();
        OneTimeTokenDto dtoFromRedis = OneTimeTokenDto.builder()
                .userToken(token) // Токен должен совпадать
                .used(false)
                .build();

        Mockito.when(valueOperations.get(token)).thenReturn(dtoFromRedis);
        Mockito.when(objectMapper.convertValue(dtoFromRedis, OneTimeTokenDto.class)).thenReturn(dtoFromRedis); // Настройка ObjectMapper

        OneTimeTokenDto inputDto = new OneTimeTokenDto();
        inputDto.setUserToken(token);

        tokenService.verifyToken(inputDto);

        Assertions.assertTrue(dtoFromRedis.getUsed());
        Mockito.verify(redisTemplate).delete(token);
        Mockito.verify(kafkaSenderService).sendToTopic(any(), eq(dtoFromRedis));
    }

    @Test
    void verifyToken_WhenTokenDoesNotExist_ShouldThrowException() {
        String token = "invalid-token";
        Mockito.when(valueOperations.get(token)).thenReturn(null);

        OneTimeTokenDto inputDto = new OneTimeTokenDto();
        inputDto.setUserToken(token);

        Assertions.assertThrows(TokenNotExistException.class, () -> tokenService.verifyToken(inputDto));
    }

    @Test
    void verifyToken_WhenTokenExpired_ShouldUpdateRedis() throws TokenNotExistException {
        String receivedToken = UUID.randomUUID().toString();
        String differentToken = UUID.randomUUID().toString();
        OneTimeTokenDto dtoFromRedis = OneTimeTokenDto.builder()
                .userToken(differentToken)
                .used(false)
                .build();

        Mockito.when(valueOperations.get(receivedToken)).thenReturn(dtoFromRedis);
        Mockito.when(objectMapper.convertValue(dtoFromRedis, OneTimeTokenDto.class)).thenReturn(dtoFromRedis);
        Mockito.when(redisTemplate.getExpire(receivedToken, TimeUnit.MILLISECONDS)).thenReturn(5000L);

        OneTimeTokenDto inputDto = new OneTimeTokenDto();
        inputDto.setUserToken(receivedToken);

        tokenService.verifyToken(inputDto);

        Mockito.verify(valueOperations).set(
                eq(receivedToken),
                eq(dtoFromRedis),
                eq(5000L),
                eq(TimeUnit.MILLISECONDS)
        );
    }
}