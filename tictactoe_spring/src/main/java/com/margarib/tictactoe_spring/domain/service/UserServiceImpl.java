package com.margarib.tictactoe_spring.domain.service;

import com.margarib.tictactoe_spring.datasource.mapper.UserEntityMapper;
import com.margarib.tictactoe_spring.datasource.model.UserEntity;
import com.margarib.tictactoe_spring.datasource.repository.UserRepository;
import com.margarib.tictactoe_spring.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public boolean isUserExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public void createUser(String userName, String password) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        userRepository.save(userEntityMapper.toUserEntity(user));
    }

    @Override
    public User getUserByUUID(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return userEntityMapper.toUser(userEntity);
    }

    @Override
    public User getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return userEntityMapper.toUser(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
