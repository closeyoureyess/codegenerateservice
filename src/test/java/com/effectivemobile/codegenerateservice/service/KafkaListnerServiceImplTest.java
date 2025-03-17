package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.entity.CustomUser;
import com.effectivemobile.codegenerateservice.entity.OneTimeTokenDto;
import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class KafkaListnerServiceImplTest {

    @Mock
    private OneTimeTokenService oneTimeTokenService;

    private KafkaListnerServiceImpl kafkaListenerService;

    @BeforeEach
    void setUp() {
        kafkaListenerService = new KafkaListnerServiceImpl(oneTimeTokenService);
    }

    @Test
    void listenEmail_ShouldCallCreateToken() {
        CustomUser user = new CustomUser();
        user.setEmail("test@example.com");
        kafkaListenerService.listenEmail(user);
        Mockito.verify(oneTimeTokenService, times(1)).createToken(user);
    }

    @Test
    void listenToken_ShouldCallVerifyToken() throws TokenNotExistException {
        OneTimeTokenDto tokenDto = OneTimeTokenDto.builder().userToken("token").build();
        kafkaListenerService.listenToken(tokenDto);
        Mockito.verify(oneTimeTokenService, times(1)).verifyToken(tokenDto);
    }

    @Test
    void listenToken_WhenExceptionThrown_ShouldPropagate() throws TokenNotExistException {
        OneTimeTokenDto tokenDto = new OneTimeTokenDto();
        Mockito.doThrow(new TokenNotExistException("Ошибка")).when(oneTimeTokenService).verifyToken(tokenDto);
        Assertions.assertThrows(
                TokenNotExistException.class,
                () -> kafkaListenerService.listenToken(tokenDto)
        );
    }

}
