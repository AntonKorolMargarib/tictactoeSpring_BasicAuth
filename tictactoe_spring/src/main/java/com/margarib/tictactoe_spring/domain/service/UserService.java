package com.margarib.tictactoe_spring.domain.service;

import com.margarib.tictactoe_spring.domain.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;


public interface UserService extends UserDetailsService {
    boolean isUserExist(String username);
    void createUser(String username, String password);
    User getUserByUUID(UUID id);
    User getUserByUsername(String username);

}
