package com.margarib.tictactoe_spring.web.controller;

import com.margarib.tictactoe_spring.domain.model.Game;
import com.margarib.tictactoe_spring.domain.model.User;
import com.margarib.tictactoe_spring.domain.service.GameService;
import com.margarib.tictactoe_spring.domain.service.UserService;
import com.margarib.tictactoe_spring.gameStates.GameState;
import com.margarib.tictactoe_spring.web.mapper.GameWebMapper;
import com.margarib.tictactoe_spring.web.model.GameDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final GameWebMapper gameWebMapper;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/gameMenu")
    public String gameMenu(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        model.addAttribute("login", user.getUsername());

        List<GameDTO> playerGamesHistory = gameService.getGamesHistory(user.getId())
                .stream().map(gameWebMapper::toGameDTO).toList();
        model.addAttribute("playerGamesHistory", playerGamesHistory);
        List<Game> availableGames = gameService.getAvailableMultiGames(user.getId());
        List<GameDTO> availableGamesDTO = availableGames.stream().map(gameWebMapper::toGameDTO).toList();
        model.addAttribute("availableGames", availableGamesDTO);
        return "gameMenu";
    }

    @GetMapping("/newSingleGame")
    public String newSingleGame(RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        try {
            GameDTO currentGame = gameWebMapper.toGameDTO(gameService.createNewGame(user.getId(), true));
            return "redirect:/game/" + currentGame.getGameId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating single game: " + e.getMessage());
            return "redirect:/gameMenu";
        }
    }

    @GetMapping("/newMultiGame")
    public String newMultiGame(RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        try {
            GameDTO currentGame = gameWebMapper.toGameDTO(gameService.createNewGame(user.getId(), false));
            System.out.println(currentGame);
            return "redirect:/game/multi/" + currentGame.getGameId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating multi game: " + e.getMessage());
            return "redirect:/gameMenu";
        }
    }

    @PostMapping("/game/multi/{gameId}/join")
    public String joinGame(@PathVariable("gameId") UUID gameId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());

        List<Game> availableGames = gameService.getAvailableMultiGames(user.getId());
        boolean gameIsAvailable = availableGames.stream().anyMatch(game -> game.getGameId().equals(gameId));

        if (!gameIsAvailable) {
            redirectAttributes.addFlashAttribute("errorMessage", "This game is not available for joining.");
            return "redirect:/gameMenu";
        }

        GameDTO game = gameWebMapper.toGameDTO(gameService.joinGame(gameId, user.getId()));

        sendGameUpdate(gameId, game);

        return "redirect:/game/multi/" + gameId;
    }

    @GetMapping("/game/{gameId}")
    public String getSingleGame(@PathVariable("gameId") UUID gameId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        GameDTO game = gameWebMapper.toGameDTO(gameService.getGame(gameId));
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        model.addAttribute("login", authentication.getName());
        model.addAttribute("gameId", game.getGameId());
        model.addAttribute("board", game.getBoard().getBoard());
        model.addAttribute("winner", game.getWinner());
        return "game";
    }

    @GetMapping("/game/multi/{gameId}")
    public String getMultiGame(@PathVariable("gameId") UUID gameId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        GameDTO game = gameWebMapper.toGameDTO(gameService.getGame(gameId));
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        model.addAttribute("login", authentication.getName());
        model.addAttribute("gameId", game.getGameId());
        model.addAttribute("board", game.getBoard().getBoard());
        model.addAttribute("status", game.getState());
        model.addAttribute("gameOver", game.isGameOver());
        model.addAttribute("winner", game.getWinner());
        return "multiGame";
    }


    @PostMapping("/game/single/{gameId}")
    public String updateGame(@PathVariable("gameId") UUID gameId,
                             @RequestParam("row") int row,
                             @RequestParam("col") int col,
                             Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Game game = gameService.getGame(gameId);

        int [][] board = game.getBoard().getBoard();

        if (gameService.validateGameBoard(board, row, col)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid move");
        }

        board[row][col] = 1;
        game.getBoard().setBoard(board);
        gameService.saveGame(game);

        String gameOverResult = gameService.isGameOver(game);

        if (gameOverResult != null) {
            model.addAttribute("login", authentication.getName());
            model.addAttribute("gameId", game.getGameId());
            model.addAttribute("board", game.getBoard().getBoard());
            model.addAttribute("winner", gameOverResult);
            return "game";
        }

        Game computerGame = gameService.getMinimaxTurn(game);
        gameService.saveGame(computerGame);

        gameOverResult = gameService.isGameOver(computerGame);

        if (gameOverResult != null) {
            model.addAttribute("login", authentication.getName());
            model.addAttribute("gameId", computerGame.getGameId());
            model.addAttribute("board", computerGame.getBoard().getBoard());
            model.addAttribute("winner", gameOverResult);
            return "game";
        }

        model.addAttribute("login", authentication.getName());
        model.addAttribute("gameId", computerGame.getGameId());
        model.addAttribute("board", computerGame.getBoard().getBoard());

        return "game";

    }

    @PostMapping("/game/multi/{gameId}")
    public String updateMultiGame(@PathVariable("gameId") UUID gameId,
                                  @RequestParam("row") int row,
                                  @RequestParam("col") int col,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());

        Game game = gameService.getGame(gameId);

        if (game.getState().equals(GameState.WAITING_FOR_PLAYER)) {
            redirectAttributes.addFlashAttribute("error", "Game is waiting for player");
            return "redirect:/game/multi/" + gameId;
        }
        if (!gameService.validateTurn(gameId, user.getId(), row, col)) {
            redirectAttributes.addFlashAttribute("error", "Wrong move! Another player turn!");
            return "redirect:/game/multi/" + gameId;
        }

        game = gameService.makeMultiTurn(gameId, row, col);
        String gameOverResult = gameService.isGameOver(game);

        GameDTO updatedGame = gameWebMapper.toGameDTO(game);

        if (gameOverResult != null) {
            updatedGame.setGameOver(true);
            updatedGame.setWinner(gameOverResult);

            model.addAttribute("login", authentication.getName());
            model.addAttribute("gameId", updatedGame.getGameId());
            model.addAttribute("board", updatedGame.getBoard().getBoard());
            model.addAttribute("gameOver", true);
            model.addAttribute("winner", gameOverResult);
            model.addAttribute("status", updatedGame.getState().toString());
            sendGameUpdate(gameId, updatedGame);
            return "multiGame";
        }


        model.addAttribute("login", authentication.getName());
        model.addAttribute("gameId", updatedGame.getGameId());
        model.addAttribute("board", updatedGame.getBoard().getBoard());
        model.addAttribute("status", game.getState().toString());
        sendGameUpdate(gameId, updatedGame);

        return "multiGame";
    }

    private void sendGameUpdate(UUID gameId, GameDTO gameDTO) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId, gameDTO);
    }

}
