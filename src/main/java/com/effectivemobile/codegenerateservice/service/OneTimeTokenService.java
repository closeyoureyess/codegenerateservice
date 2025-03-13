package com.effectivemobile.codegenerateservice.service;

import com.effectivemobile.codegenerateservice.exeptions.TokenNotExistException;

public interface OneTimeTokenService {

    void createToken(String email);

    void verifyToken(String token) throws TokenNotExistException;
}
