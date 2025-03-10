package sk.tuke.kpi.kp.checkers.core;

import org.w3c.dom.ls.LSOutput;

import java.util.Random;

public class Bot {
    private Random random = new Random();

    private boolean hasCaptureMove(Field field, int fromRow, int fromCol) {
        int[][] directions = {{-2, 2}, {-2, -2}, {2, -2}, {2, 2}};
        for (int[] dir : directions) {
            int toRow = fromRow + dir[0];
            int toCol = fromCol + dir[1];
            if (field.isValidMove(fromRow, fromCol, toRow, toCol)) {
                return true;
            }
        }
        return false;
    }

    public void makeMove(Field field) {
        boolean moveMade = false;

        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                if (hasCaptureMove(field, fromRow, fromCol)) {
                    int[][] directions = {{-2, 2}, {-2, -2}, {2, -2}, {2, 2}};
                    for (int[] dir : directions) {
                        int toRow = fromRow + dir[0];
                        int toCol = fromCol + dir[1];
                        if (field.isValidMove(fromRow, fromCol, toRow, toCol)) {
                            moveMade = field.move(fromRow, fromCol, toRow, toCol);
                            if (moveMade) {
                                System.out.println("Black captures from " + (char) ('a' + fromCol) + (8 - fromRow) + " to " + (char) ('a' + toCol) + (8 - toRow));
                                if (!field.canContinueCapture()) {
                                    System.out.println("change canContinueCapture when capture move");
                                    field.switchTurn();
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }

        // Если нет захвата, сделать случайный ход
        while (!moveMade) {
            int fromRow = random.nextInt(8);
            int fromCol = random.nextInt(8);
            int toRow = random.nextInt(8);
            int toCol = random.nextInt(8);

            if (field.isValidMove(fromRow, fromCol, toRow, toCol)) {
                moveMade = field.move(fromRow, fromCol, toRow, toCol);
                if (moveMade) {
                    System.out.println("Black moves from " + (char) ('a' + fromCol) + (8 - fromRow) + " to " + (char) ('a' + toCol) + (8 - toRow));
                    if (!field.canContinueCapture()) {
                        System.out.println("change canContinueCapture when regular move");
                        field.switchTurn();
                        return;
                    }
                }
            }
        }
    }
}
