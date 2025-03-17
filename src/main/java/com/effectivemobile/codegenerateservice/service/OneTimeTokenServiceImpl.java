package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.effectivemobile.codegenerateservice.constants.Constants.EXPIRE_TIME;

@Service
@Validated
public class OneTimeTokenServiceImpl implements OneTimeTokenService {

    @Value("${kafka.producer.topic-name.token-is-valid}")
    private String listenTokenIsValidTopicName;

    private final ObjectMapper objectMapper;

    private final RedisTemplate<Object, Object> redisTemplate;

    private final KafkaSenderService kafkaSenderService;

    @Autowired
    public OneTimeTokenServiceImpl(RedisTemplate<Object, Object> redisTemplate, KafkaSenderService kafkaSenderService,
                                   @Qualifier("redisConfig") ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.kafkaSenderService = kafkaSenderService;
        this.objectMapper = objectMapper;
    }

    @Override
    public OneTimeTokenDto createToken(CustomUser customUser) {
        String token = UUID.randomUUID().toString();
        String email = customUser.getEmail();
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime expireDate = createDate.plusMinutes(EXPIRE_TIME);
        Boolean isUsed = false;
        OneTimeTokenDto localOneTimeTokenDto = OneTimeTokenDto.builder()
                .userToken(token)
                .email(email)
                .createdTime(createDate)
                .expiredTime(expireDate)
                .used(isUsed)
                .build();
        redisTemplate.opsForValue().set(token, localOneTimeTokenDto, EXPIRE_TIME, TimeUnit.MINUTES);
        return localOneTimeTokenDto;
    }

    @Override
    public void verifyToken(OneTimeTokenDto oneTimeTokenDto) throws TokenNotExistException {
        OneTimeTokenDto oneTimeTokenDtoFromRedis;
        String receivedToken = oneTimeTokenDto.getUserToken();
        Object objectFromRedis = redisTemplate.opsForValue().get(receivedToken);
        if (objectFromRedis != null) {
            oneTimeTokenDtoFromRedis = objectMapper.convertValue(objectFromRedis, OneTimeTokenDto.class);
        } else {
            throw new TokenNotExistException(ExceptionsDescription.TOKEN_NOT_FOUND.getDescription());
        }
        if (oneTimeTokenDtoFromRedis.getUserToken() != null &&
                oneTimeTokenDtoFromRedis.getUserToken().equals(receivedToken)) {
            oneTimeTokenDtoFromRedis.setUsed(Boolean.TRUE);
            redisTemplate.delete(receivedToken);
        } else if(oneTimeTokenDtoFromRedis.getUserToken() != null) {
            Long expireTimeInRedis = redisTemplate.getExpire(receivedToken, TimeUnit.MILLISECONDS);
            if (expireTimeInRedis != null && expireTimeInRedis > 0) {
                oneTimeTokenDtoFromRedis.setUsed(Boolean.FALSE);
                redisTemplate.opsForValue().set(receivedToken, oneTimeTokenDtoFromRedis, expireTimeInRedis, TimeUnit.MILLISECONDS);
            }
        }
        kafkaSenderService.sendToTopic(listenTokenIsValidTopicName, oneTimeTokenDtoFromRedis);
    }
}