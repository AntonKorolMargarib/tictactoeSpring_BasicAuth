package com.margarib.tictactoe_spring.domain.service;

import com.margarib.tictactoe_spring.datasource.mapper.GameEntityMapper;
import com.margarib.tictactoe_spring.datasource.model.GameEntity;
import com.margarib.tictactoe_spring.datasource.repository.GameRepository;
import com.margarib.tictactoe_spring.domain.model.Game;
import com.margarib.tictactoe_spring.domain.model.GameBoard;
import com.margarib.tictactoe_spring.gameStates.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;

    @Override
    public Game createNewGame(UUID playerXId, boolean vsComputer) {
        Game game = new Game(
                new GameBoard(),
                vsComputer,
                playerXId,
                null
        );

        game = GameEntityMapper.INSTANCE.toGame(gameRepository.save(GameEntityMapper.INSTANCE.toGameEntity(game)));
        return game;
    }

    @Override
    public Game getMinimaxTurn(Game game) {
        int[][] board = game.getBoard().getBoard();
        int[] bestMove = Minimax.findBestMove(board);

        if (bestMove != null) {
            board[bestMove[0]][bestMove[1]] = 2; // Компьютер ставит 2
        }
        game.getBoard().setBoard(board);
        game.setState(GameState.PLAYER_X_TURN);
        gameRepository.save(GameEntityMapper.INSTANCE.toGameEntity(game));

        return game;
    }

    @Override
    public boolean validateGameBoard(int[][] currentBoard, int row, int col) {
        return currentBoard[row][col] != 0;
    }

    @Override
    public Game getGame(UUID gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        if (gameEntity == null) return null;
        return GameEntityMapper.INSTANCE.toGame(gameEntity);
    }

    @Override
    public List<Game> getGamesHistory(UUID playerId) {
        List<GameEntity> availableGames = (List<GameEntity>) gameRepository.findAll();
        return availableGames.stream()
                .filter(game -> game.getPlayerX().getId().equals(playerId)) // ToDO отображение онлайн игр, где игрок playerO
                .filter(game -> game.getState().equals(GameState.DRAW)
                || game.getState().equals(GameState.PLAYER_X_WIN)
                || game.getState().equals(GameState.PLAYER_O_WIN))
                .map(GameEntityMapper.INSTANCE::toGame)
                .toList();
    }

    @Override
    @Transactional
    public Game makeMultiTurn(UUID gameId, int row, int col) {

        Game game = getGame(gameId);
        game.getBoard().getBoard()[row][col] = game.getState().equals(GameState.PLAYER_X_TURN) ? 1 : 2;
        GameState nextGameState = game.getState() == GameState.PLAYER_X_TURN ? GameState.PLAYER_O_TURN : GameState.PLAYER_X_TURN;
        game.setState(nextGameState);
        gameRepository.save(GameEntityMapper.INSTANCE.toGameEntity(game));
        return game;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateTurn(UUID gameId, UUID playerId, int row, int column) {
        Game game = getGame(gameId);
        GameState turnState = game.getPlayerX().equals(playerId) ? GameState.PLAYER_X_TURN : GameState.PLAYER_O_TURN;
        return game.getState().equals(turnState) && game.getBoard().getBoard()[row][column] == 0;
    }

    @Override
    @Transactional
    public List<Game> getAvailableMultiGames(UUID playerX) {
        return gameRepository.findAvailableMultiGames(playerX)
                .stream()
                .map(GameEntityMapper.INSTANCE::toGame)
                .toList();
    }

    @Override
    @Transactional
    public Game joinGame(UUID gameId, UUID playerId) {
        Game game = GameEntityMapper.INSTANCE.toGame(gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found")));

        game.setPlayerO(playerId);
        game.setState(GameState.PLAYER_X_TURN);
        gameRepository.save(GameEntityMapper.INSTANCE.toGameEntity(game));
        return game;
    }

    @Override
    public String isGameOver(Game game) {
        int[][] board = game.getBoard().getBoard();
        String secondPlayer = (game.isVsComputer() ? "Computer" : "PlayerO");

        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                String winner = (board[i][0] == 1) ? "PlayerX wins!" : "%s wins!".formatted(secondPlayer);
                setGameOverState(winner, game);
                return winner;
            }
            if (board[0][i] != 0 && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                String winner = (board[i][0] == 1) ? "PlayerX wins!" : "%s wins!".formatted(secondPlayer);
                setGameOverState(winner, game);
                return winner;
            }
        }

        // Check diagonals
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            String winner = (board[0][0] == 1) ? "PlayerX wins!" : "%s wins!".formatted(secondPlayer);
            setGameOverState(winner, game);
            return winner;
        }
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            String winner = (board[2][0] == 1) ? "PlayerX wins!" : "%s wins!".formatted(secondPlayer);
            setGameOverState(winner, game);
            return winner;
        }

        // Check for a draw
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) return null; // Game not over
            }
        }
        saveGame(game);
        return "It's a draw!";
    }

    private void setGameOverState(String winner, Game game) {
        if (winner.equals("PlayerX wins!")) {
            game.setState(GameState.PLAYER_X_WIN);
        } else {
            game.setState(GameState.PLAYER_O_WIN);
        }
        saveGame(game);
    }

    @Override
    public void saveGame(Game game) {
        GameEntity gameEntity = GameEntityMapper.INSTANCE.toGameEntity(game);
        gameRepository.save(gameEntity);
    }

}
