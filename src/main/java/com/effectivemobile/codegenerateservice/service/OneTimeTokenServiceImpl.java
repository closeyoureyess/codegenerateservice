package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeToken;
import com.effectivemobile.codegenerateservice.exeptions.ExceptionsDescription;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final RedisTemplate<String, Object> redisTemplate;

    private final KafkaSenderService kafkaSenderService;

    @Autowired
    public OneTimeTokenServiceImpl(RedisTemplate<String, Object> redisTemplate, KafkaSenderService kafkaSenderService) {
        this.redisTemplate = redisTemplate;
        this.kafkaSenderService = kafkaSenderService;
    }

    @Override
    public OneTimeToken createToken(CustomUser customUser) {
        String token = UUID.randomUUID().toString();
        String email = customUser.getEmail();
        LocalDateTime createDate = LocalDateTime.now();
        LocalDateTime expireDate = createDate.plusMinutes(EXPIRE_TIME);
        Boolean isUsed = false;
        OneTimeToken localOneTimeToken = OneTimeToken.builder()
                .userToken(token)
                .email(email)
                .createdTime(createDate)
                .expiredTime(expireDate)
                .used(isUsed)
                .build();
        redisTemplate.opsForValue().set(token, localOneTimeToken, EXPIRE_TIME, TimeUnit.MINUTES);
        return localOneTimeToken;
    }

    @Override
    public void verifyToken(OneTimeToken oneTimeToken) throws TokenNotExistException {
        OneTimeToken oneTimeTokenFromRedis;
        String receivedToken = oneTimeToken.getUserToken();
        Object objectFromRedis = redisTemplate.opsForValue().get(receivedToken);
        if (objectFromRedis != null) {
            oneTimeTokenFromRedis = (OneTimeToken) objectFromRedis;
        } else {
            throw new TokenNotExistException(ExceptionsDescription.TOKEN_NOT_FOUND.getDescription());
        }
        if (oneTimeTokenFromRedis.getUserToken() != null &&
                oneTimeTokenFromRedis.getUserToken().equals(receivedToken)) {
            oneTimeTokenFromRedis.setUsed(Boolean.TRUE);
            redisTemplate.delete(receivedToken);
        } else if(oneTimeTokenFromRedis.getUserToken() != null) {
            Long expireTimeInRedis = redisTemplate.getExpire(receivedToken, TimeUnit.MILLISECONDS);
            if (expireTimeInRedis != null && expireTimeInRedis > 0) {
                oneTimeTokenFromRedis.setUsed(Boolean.FALSE);
                redisTemplate.opsForValue().set(receivedToken, oneTimeTokenFromRedis, expireTimeInRedis, TimeUnit.MILLISECONDS);
            }
        }
        kafkaSenderService.sendToTopic(listenTokenIsValidTopicName, oneTimeTokenFromRedis);
    }
}