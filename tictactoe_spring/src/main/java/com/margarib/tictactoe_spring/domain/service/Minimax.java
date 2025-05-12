package com.margarib.tictactoe_spring.domain.service;

public class Minimax {
    private static final int COMPUTER = 2;
    private static final int PLAYER = 1;

    public static int[] findBestMove(int[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Проверяем, свободно ли место
                if (board[i][j] == 0) {
                    // Делаем ход
                    board[i][j] = COMPUTER;
                    // Вызываем minimax, чтобы получить оценку этого хода
                    int score = minimax(board, 0, false);
                    // Отменяем ход
                    board[i][j] = 0;

                    // Если оценка этого хода лучше, чем лучшая оценка на данный момент
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[] {i, j};
                    }
                }
            }
        }
        return bestMove;
    }

    private static int minimax(int[][] board, int depth, boolean isMaximizing) {
        int result = evaluate(board);
        if (result != 0) {
            return result;
        }

        if (isBoardFull(board)) {
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // Проверяем, свободно ли место
                    if (board[i][j] == 0) {
                        // Делаем ход
                        board[i][j] = COMPUTER;
                        // Вызываем minimax рекурсивно и выбираем максимум
                        int score = minimax(board, depth + 1, false);
                        // Отменяем ход
                        board[i][j] = 0;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // Проверяем, свободно ли место
                    if (board[i][j] == 0) {
                        // Делаем ход
                        board[i][j] = PLAYER;
                        // Вызываем minimax рекурсивно и выбираем минимум
                        int score = minimax(board, depth + 1, true);
                        // Отменяем ход
                        board[i][j] = 0;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private static int evaluate(int[][] board) {
        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == COMPUTER) {
                    return +10;
                } else if (board[i][0] == PLAYER) {
                    return -10;
                }
            }
        }

        // Проверка столбцов
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == COMPUTER) {
                    return +10;
                } else if (board[0][i] == PLAYER) {
                    return -10;
                }
            }
        }

        // Проверка диагоналей
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == COMPUTER) {
                return +10;
            } else if (board[0][0] == PLAYER) {
                return -10;
            }
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == COMPUTER) {
                return +10;
            } else if (board[0][2] == PLAYER) {
                return -10;
            }
        }
        // Ничья или игра продолжается
        return 0;
    }

    private static boolean isBoardFull(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return false; // Есть еще пустые клетки
                }
            }
        }
        return true; // Все клетки заполнены
    }
}
