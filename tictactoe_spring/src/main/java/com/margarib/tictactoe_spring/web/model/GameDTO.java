package com.margarib.tictactoe_spring.web.model;


import com.margarib.tictactoe_spring.gameStates.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private UUID gameId;
    private GameBoardDTO board;
    private GameState state;
    private boolean gameOver;
    private String winner;
}
