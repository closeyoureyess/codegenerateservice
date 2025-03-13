package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.effectivemobile.codegenerateservice.constants.Constants.EXPIRE_TIME;

@Service
public class OneTimeTokenServiceImpl implements OneTimeTokenService {

    @Value("${kafka.topic-name.tokenObject}")
    private final String stringTopicName;

    @Value("${kafka.topic-name.booleanVerifyToken}")
    private final String booleanTopicName;

    private final RedisTemplate<String, Object> redisTemplate;

    private final KafkaSenderService kafkaSenderService;

    @Autowired
    public OneTimeTokenServiceImpl(RedisTemplate<String, Object> redisTemplate, KafkaSenderService kafkaSenderService, String stringTopicName,
                                   String booleanTopicName) {
        this.redisTemplate = redisTemplate;
        this.kafkaSenderService = kafkaSenderService;
        this.stringTopicName = stringTopicName;
        this.booleanTopicName = booleanTopicName;
    }

    @Override
    public void createToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime expireDate = createDate.plusMinutes(EXPIRE_TIME);
        Boolean isUsed = false;
        OneTimeTokenDto oneTimeTokenDto = OneTimeTokenDto.builder()
                .userToken(token)
                .email(email)
                .createdTime(createDate)
                .expiredTime(expireDate)
                .used(isUsed)
                .build();
        redisTemplate.opsForValue().set(token, oneTimeTokenDto, EXPIRE_TIME, TimeUnit.MINUTES);
        kafkaSenderService.sendToTopic(stringTopicName, oneTimeTokenDto);
    }

    @Override
    public void verifyToken(String token) throws TokenNotExistException {
        OneTimeTokenDto oneTimeTokenDto;
        Object object = redisTemplate.opsForValue().get(token);
        if (object != null) {
            oneTimeTokenDto = (OneTimeTokenDto) object;
        } else {
            throw new TokenNotExistException(ExceptionsDescription.TOKEN_NOT_FOUND.getDescription());
        }
        Boolean result;
        if (oneTimeTokenDto.getUserToken() != null &&
                oneTimeTokenDto.getUserToken().equals(token)) {
            oneTimeTokenDto.setUsed(true);
            redisTemplate.opsForValue().set(token, oneTimeTokenDto, EXPIRE_TIME, TimeUnit.MINUTES);
            result = Boolean.TRUE;
        } else {
            result = Boolean.FALSE;
        }
        kafkaSenderService.sendToTopic(booleanTopicName, result);
    }
}