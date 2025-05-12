package com.margarib.tictactoe_spring.domain.service;

import com.margarib.tictactoe_spring.web.model.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean register(SignUpRequest signUpRequest) {
        if(userService.isUserExist(signUpRequest.getUserName())) return false;
        userService.createUser(signUpRequest.getUserName(), passwordEncoder.encode(signUpRequest.getPassword()));
        return true;
    }

    @Override
    public boolean isAuthenticate(String userName, String password) {
        try {
            UserDetails userDetails = userService.loadUserByUsername(userName);
            return passwordEncoder.matches(password, userDetails.getPassword());
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
}
