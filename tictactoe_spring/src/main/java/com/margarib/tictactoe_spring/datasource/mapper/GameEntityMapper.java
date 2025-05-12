package com.margarib.tictactoe_spring.datasource.mapper;

import com.margarib.tictactoe_spring.datasource.model.GameEntity;
import com.margarib.tictactoe_spring.datasource.model.UserEntity;
import com.margarib.tictactoe_spring.domain.model.Game;
import com.margarib.tictactoe_spring.domain.model.GameBoard;
import java.io.*;
import java.util.UUID;

//Возможный вариант кастомный
public class GameEntityMapper {

    public static final GameEntityMapper INSTANCE = new GameEntityMapper();

    private GameEntityMapper() {}

    public GameEntity toGameEntity(Game game) {
        byte[] byteBoard = convertBoardToBytes(game.getBoard().getBoard());
        UserEntity mainPlayer = new UserEntity();
        mainPlayer.setId(game.getPlayerX());
        UserEntity secondPlayer = null;
        if (game.getPlayerO() != null) {
            secondPlayer = new UserEntity();
            secondPlayer.setId(game.getPlayerO());
        }
        return new GameEntity(game.getGameId(),
                byteBoard,
                game.isVsComputer(),
                game.getState(),
                mainPlayer,
                secondPlayer);
    }

    public Game toGame(GameEntity gameEntity) {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setBoard(convertBytesToBoard(gameEntity.getBoard()));
        UUID mainPlayer = gameEntity.getPlayerX().getId();
        UUID secondPlayer = null;
        if (gameEntity.getPlayerO() != null) {
            secondPlayer = gameEntity.getPlayerO().getId();
        }
        return new Game(gameEntity.getGameId(),
                gameBoard,
                gameEntity.isVsComputer(),
                gameEntity.getState(),
                mainPlayer,
                secondPlayer);
    }

    private byte[] convertBoardToBytes(int[][] board) {
        if (board == null) return null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(board);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error converting field to bytes", e);
        }
    }

    private int[][] convertBytesToBoard(byte[] bytes) {
        if (bytes == null) return null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (int[][]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error converting bytes to field", e);
        }
    }

}

