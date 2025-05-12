package com.margarib.tictactoe_spring.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
public class GameBoard {
    private int[][] board;

    public GameBoard() {
        this.board = new int[3][3];
        for (int i = 0; i < 3; i++) {
            Arrays.fill(board[i], 0); // Initialize with 0
        }
    }
}
