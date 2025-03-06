package sk.tuke.kpi.checkers.consoleui;

import sk.tuke.kpi.checkers.core.Field;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private Field field;

    public ConsoleUI(Field field) {
        this.field = field;
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
        System.out.println();
        System.out.println("              * * * * * *");
        System.out.println("              * PLAYING *");
        System.out.println("              * * * * * *");
        System.out.println();

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

    private void handleInput() {
        if (field.isWhiteTurn()) {
            System.out.println("Ход белых.");
        } else {
            System.out.println("Ход черных.");
        }

        System.out.println("Введите ход (пример: e3 d4): ");
        String move = input.nextLine().trim().toLowerCase();

        if (move.equals("exit")) {
            field.endGame();
            return;
        }

        String[] parts = move.split(" ");
        if (parts.length != 2 || parts[0].length() != 2 || parts[1].length() != 2) {
            System.out.println("Неверный формат! Введите, например: e3 d4");
            return;
        }

        try {
            int fromRow = 8 - Character.getNumericValue(parts[0].charAt(1));
            int fromCol = parts[0].charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(parts[1].charAt(1));
            int toCol = parts[1].charAt(0) - 'a';

            if (!field.move(fromRow, fromCol, toRow, toCol)) {
                System.out.println("Невозможный ход!");
            } else {
                if (!field.canContinueCapture()) {
                    field.switchTurn();
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода! Проверьте формат и попробуйте снова.");
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

