package com.margarib.tictactoe_spring.domain.model;

import com.margarib.tictactoe_spring.gameStates.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    private UUID gameId;
    private GameBoard board;
    private boolean vsComputer;
    private GameState state;
    private UUID playerX;
    private UUID playerO;

    public Game(GameBoard board, boolean vsComputer, UUID playerX, UUID playerO) {
        this.gameId = UUID.randomUUID();
        this.board = board;
        this.vsComputer = vsComputer;
        this.state = vsComputer ? GameState.PLAYER_X_TURN : GameState.WAITING_FOR_PLAYER;
        this.playerX = playerX;
        this.playerO = playerO;
    }
}

