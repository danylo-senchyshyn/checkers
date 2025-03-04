package sk.tuke.kpi.checkers.consoleui;

import sk.tuke.kpi.checkers.core.Field;

import java.util.Scanner;

public class ConsoleUI {
    private Scanner input = new Scanner(System.in);
    private Field field;

    public ConsoleUI(Field field) {
        this.field = field;
    }

    public void printBoard() {
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

    public void play () {
        while (true) {
            printBoard();

            if (field.isWhiteTurn()) {
                System.out.println("Ход белых.");
            } else {
                System.out.println("Ход черных.");
            }

            System.out.println("Введите ход (пример: e3 d4): ");
            String move = input.nextLine();

            if (move.equals("exit")) break;

            String[] parts = move.split(" ");
            if (parts.length != 2) {
                System.out.println("Неверный формат!");
                continue;
            }

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
        }
    }
}
