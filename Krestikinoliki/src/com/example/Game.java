package com.example;

import android.widget.Toast;

/**
 * Created by sharlamov on 18.08.2015.
 */
public class Game {
    /**
     * игроки
     */
    private Player[] players;
    /**
     * поле
     */
    private Square[][] field;
    /**
     * начата ли игра?
     */
    private boolean started;
    /**
     * текущий игрок
     */
    private Player activePlayer;
    /**
     * Считает колличество заполненных ячеек
     */
    private int filled;
    /**
     * Всего ячеек
     */
    private int squareCount;

    private WinnerCheckerInterface[] winnerCheckers;


    /**
     * Конструктор
     */
    public Game() {
        winnerCheckers = new WinnerCheckerInterface[4];
        winnerCheckers[0] = new WinnerCheckerHorizontal(this);
        winnerCheckers[1] = new WinnerCheckerVertical(this);
        winnerCheckers[2] = new WinnerCheckerDiagonalLeft(this);
        winnerCheckers[3] = new WinnerCheckerDiagonalRight(this);

        field = new Square[3][3];
        squareCount = 0;
        // заполнение поля
        for (int i = 0, l = field.length; i < l; i++) {
            for (int j = 0, l2 = field[i].length; j < l2; j++) {
                field[i][j] = new Square();
                squareCount++;
            }
        }
        players = new Player[2];
        started = false;
        activePlayer = null;
        filled = 0;
    }

    public void start() {
        resetPlayers();
        started = true;
    }

    private void resetPlayers() {
        players[0] = new Player("X");
        players[1] = new Player("O");
        setCurrentActivePlayer(players[0]);
    }

    public Square[][] getField() {
        return field;
    }

    private void setCurrentActivePlayer(Player player) {
        activePlayer = player;
    }

    public boolean makeTurn(int x, int y) {
        if (field[x][y].isFilled()) {
            return false;
        }
        field[x][y].fill(getCurrentActivePlayer());
        filled++;
        switchPlayers();
        return true;
    }

    private void switchPlayers() {
        activePlayer = (activePlayer == players[0]) ? players[1] : players[0];
    }

    public Player getCurrentActivePlayer() {
        return activePlayer;
    }

    public boolean isFieldFilled() {
        return squareCount == filled;
    }

    public void reset() {
        resetField();
        resetPlayers();
    }

    private void resetField() {
        for (int i = 0, l = field.length; i < l; i++) {
            for (int j = 0, l2 = field[i].length; j < l2; j++) {
                field[i][j].fill(null);
            }
        }
        filled = 0;
    }

    public Player checkWinner() {
        for (WinnerCheckerInterface winChecker : winnerCheckers) {
            Player winner = winChecker.checkWinner();
            if (winner != null) {
                return winner;
            }
        }
        return null;
    }
}