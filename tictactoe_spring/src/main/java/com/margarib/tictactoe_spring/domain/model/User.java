package com.margarib.tictactoe_spring.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String password;
}
