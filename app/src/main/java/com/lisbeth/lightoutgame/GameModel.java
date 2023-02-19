package com.lisbeth.lightoutgame;

public class GameModel {
    private boolean[][] board;
    private boolean[][] solution;

    public GameModel(int height, int width) {
        this.board = new boolean[height][width];
        this.solution = new boolean[height][width];
    }

    public void setBoard(boolean[][] board) {
        this.board = board;
    }

    public boolean[][] getBoard() {
        return board;
    }

    public boolean[][] getSolution() {
        return solution;
    }

    public void setSolution(boolean[][] solution) {
        this.solution = solution;
    }



}
