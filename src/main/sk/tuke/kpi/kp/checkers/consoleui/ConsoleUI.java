package sk.tuke.kpi.kp.checkers.consoleui;

import sk.tuke.kpi.kp.checkers.core.Bot;
import sk.tuke.kpi.kp.checkers.core.Field;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private Field field;
    private Bot bot;

    public ConsoleUI(Field field) {
        this.field = field;
        this.bot = new Bot();
    }

    public void play () throws InterruptedException {
        while (!field.endGame()) {
            printBoard();
            handleInput();
        }

        try {
            gameOver();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void printBoard() {
//        System.out.println();
//        System.out.println("              * * * * * *");
//        System.out.println("              * PLAYING *");
//        System.out.println("              * * * * * *");
//        System.out.println();

        System.out.println("    a   b   c   d   e   f   g   h ");
        System.out.println("   -------------------------------");

        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + "| ");
            for (int col = 0; col < 8; col++) {
                System.out.print(field.getField()[row][col] + "  ");
            }
            System.out.println("|" + (8 - row));
        }
        System.out.println("   -------------------------------");
        System.out.println("    a   b   c   d   e   f   g   h ");
        System.out.println();
    }

    private void handleInput() throws InterruptedException {
        if (field.isWhiteTurn()) {
            System.out.println("White's turn.");
            handlePlayerMove();
        } else {
            System.out.println("Black's turn.");
            bot.makeMove(field);
        }
    }

    private void handlePlayerMove() {
        System.out.println("Enter your move (e3 d4): ");
        String move = input.nextLine().trim().toLowerCase();
        if (move.equals("exit")) {
            field.endGame();
            return;
        }

        if (!move.matches("^[a-h][1-8] [a-h][1-8]$")) {
            System.out.println("Invalid format! Please enter a move like: e3 d4");
            return;
        }
        String[] parts = move.split(" ");

        try {
            int fromRow = 8 - Character.getNumericValue(parts[0].charAt(1));
            int fromCol = parts[0].charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(parts[1].charAt(1));
            int toCol = parts[1].charAt(0) - 'a';

            if (!field.move(fromRow, fromCol, toRow, toCol)) {
                System.out.println("Invalid move!");
            } else {
                if (!field.canContinueCapture()) {
                    field.switchTurn();
                }
            }
        } catch (Exception e) {
            System.out.println("Input error! Check the format and try again.");
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

