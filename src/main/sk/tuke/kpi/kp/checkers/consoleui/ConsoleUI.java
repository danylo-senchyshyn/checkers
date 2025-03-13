package sk.tuke.kpi.kp.checkers.consoleui;

import sk.tuke.kpi.kp.checkers.core.Field;
import sk.tuke.kpi.kp.checkers.core.GameState;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private final Field field;
    private boolean endGame = false;

    public ConsoleUI(Field field) {
        this.field = field;
    }

    public void play() throws InterruptedException {
        while (!field.endGame() && !endGame) {
            displayGameStats();
            printBoard();
            handleInput();
        }

        gameOver();
    }

    private void printBoard() {
        String boardHeader = "    A   B   C   D   E   F   G   H ";
        String separator = "   -------------------------------";
        System.out.println(boardHeader);
        System.out.println(separator);

        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + "| ");
            for (int col = 0; col < 8; col++) {
                System.out.print(field.getField()[row][col] + "  ");
            }
            System.out.println("|" + (8 - row));
        }
        System.out.println(separator);
        System.out.println(boardHeader);
        System.out.println();
    }

    private void displayGameStats() {
        String gameStateMessage;
        switch (field.getGameState()) {
            case PLAYING -> gameStateMessage = "PLAYING";
            case WHITE_WON -> gameStateMessage = "White won!";
            case BLACK_WON -> gameStateMessage = "Black won!";
            case DRAW -> gameStateMessage = "Game ended in a draw!";
            default -> gameStateMessage = "";
        }
        System.out.println("Game state: " + gameStateMessage);
    }

    private void handleInput() {
        System.out.println(field.isWhiteTurn() ? "White's turn." : "Black's turn.");
        handlePlayerMove();
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

            if (field.move(fromRow, fromCol, toRow, toCol)) {
                field.switchTurn();
            } else {
                System.out.println("Invalid move.");
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
