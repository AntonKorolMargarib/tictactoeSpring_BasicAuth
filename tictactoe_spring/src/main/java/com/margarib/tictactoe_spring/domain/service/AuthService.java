package com.margarib.tictactoe_spring.domain.service;

import com.margarib.tictactoe_spring.web.model.SignUpRequest;


public interface AuthService {
    boolean register(SignUpRequest signUpRequest);
    boolean isAuthenticate(String userName, String password);
}
