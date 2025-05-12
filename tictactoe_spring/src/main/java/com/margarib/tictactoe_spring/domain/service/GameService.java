package com.margarib.tictactoe_spring.domain.service;

import com.margarib.tictactoe_spring.domain.model.Game;

import java.util.List;
import java.util.UUID;

public interface GameService {

    Game getMinimaxTurn(Game game);
    boolean validateGameBoard(int [][] board, int row, int col);
    Game createNewGame(UUID playerXId, boolean vsComputer);
    boolean validateTurn(UUID gameId, UUID playerId, int row, int column);
    Game makeMultiTurn(UUID gameId, int row, int col);
    Game getGame(UUID gameId);
    List<Game> getAvailableMultiGames(UUID playerXId);
    List<Game> getGamesHistory(UUID playerXId);
    Game joinGame(UUID gameId, UUID playerOId);
    String isGameOver(Game game);
    void saveGame(Game game);
}
