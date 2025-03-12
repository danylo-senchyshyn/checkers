package sk.tuke.kpi.kp.checkers.consoleui;

import sk.tuke.kpi.kp.checkers.core.Field;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private Field field;
    private boolean endGame = false;

    public ConsoleUI(Field field) {
        this.field = field;
    }

    public void play () throws InterruptedException {
        while (!field.endGame() && !endGame) {
            gameStats();
            printBoard();
            handleInput();
        }

        try {
            printBoard();
            gameOver();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void printBoard() {
        System.out.println("    A   B   C   D   E   F   G   H ");
        System.out.println("   -------------------------------");

        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + "| ");
            for (int col = 0; col < 8; col++) {
                System.out.print(field.getField()[row][col] + "  ");
            }
            System.out.println("|" + (8 - row));
        }
        System.out.println("   -------------------------------");
        System.out.println("    A   B   C   D   E   F   G   H ");
        System.out.println();

    }

    public void gameStats() {
        if (!field.endGame()) {
            System.out.println("Game state: PLAYING");
        }
        System.out.println();
    }

    private void handleInput() throws InterruptedException {
        if (field.isWhiteTurn()) {
            System.out.println("White's turn.");
            handlePlayerMove();
        } else {
            System.out.println("Black's turn.");
            handlePlayerMove();
        }
    }

    private void handlePlayerMove() {
        System.out.println("Enter your move (e3 d4): ");
        String move = input.nextLine().trim().toLowerCase();
        if (move.equals("exit")) {
            endGame = true;
            return;
        }

        if (!move.matches("^[a-h][1-8] [a-h][1-8]$")) {
            System.out.println("Invalid format! Please enter a move like: e3 d4");
            return;
        }
        String[] parts = move.split(" ");

        try {
            System.out.println();
            int fromRow = 8 - Character.getNumericValue(parts[0].charAt(1));
            int fromCol = parts[0].charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(parts[1].charAt(1));
            int toCol = parts[1].charAt(0) - 'a';

            if (!field.move(fromRow, fromCol, toRow, toCol)) {
                System.out.println("Invalid move!");
            } else {

                field.switchTurn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gameOver() throws InterruptedException {
        String[] gameOverArt = {
                "  ██████╗  █████╗ ███╗   ███╗███████╗     ██████╗ ██╗   ██╗███████╗██████╗  ",
                " ██╔════╝ ██╔══██╗████╗ ████║██╔════╝     ██╔══██╗██║   ██║██╔════╝██╔══██╗ ",
                " ██║  ███╗███████║██╔████╔██║█████╗       ██║  ██║██║   ██║█████╗  ██████╔╝ ",
                " ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝       ██║  ██║██║   ██║██╔══╝  ██╔══██╗ ",
                " ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗     ██████╔╝╚██████╔╝███████╗██║  ██║ ",
                "  ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝ "
        };

        System.out.println();
        for (String line : gameOverArt) {
            System.out.println(line);
            Thread.sleep(200);
        }

        System.out.println("\n\nThanks for playing!");
    }
}

