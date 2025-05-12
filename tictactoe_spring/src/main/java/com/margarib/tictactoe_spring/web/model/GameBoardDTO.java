package com.margarib.tictactoe_spring.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameBoardDTO {
    private int[][] board;
}
