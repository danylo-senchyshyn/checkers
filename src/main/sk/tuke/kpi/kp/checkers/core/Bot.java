package sk.tuke.kpi.kp.checkers.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot {
    private Random random = new Random();
    public boolean moveMade;

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
        this.moveMade = false;

        // Если есть захват, сделать захват
        moveCapture(field);

        // Если нет захвата, сделать случайный ход
        if (!moveMade) {
            moveRegular(field);
        }
    }

    public void moveCapture(Field field) {
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
                                field.switchTurn();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void moveRegular(Field field) {
        while (!moveMade) {
            // Ищем все доступные ходы для черных шашек
            List<int[]> availableMoves = new ArrayList<>();
            for (int fromRow = 0; fromRow < 8; fromRow++) {
                for (int fromCol = 0; fromCol < 8; fromCol++) {
                    if (field.getField()[fromRow][fromCol].getState() == TileState.BLACK) {
                        int toRow = fromRow + 1;
                        int toCol = fromCol + (random.nextBoolean() ? 1 : -1);

                        // Проверяем, что клетка свободна и движение возможно
                        if (field.isValidMove(fromRow, fromCol, toRow, toCol)) {
                            availableMoves.add(new int[]{fromRow, fromCol, toRow, toCol});
                        }
                    }
                }
            }

            // Если есть доступные ходы, делаем случайный из них
            if (!availableMoves.isEmpty()) {
                int[] move = availableMoves.get(random.nextInt(availableMoves.size()));
                int fromRow = move[0];
                int fromCol = move[1];
                int toRow = move[2];
                int toCol = move[3];

                moveMade = field.move(fromRow, fromCol, toRow, toCol);
                if (moveMade) {
                    System.out.println("Black moves from " + (char) ('a' + fromCol) + (8 - fromRow) + " to " + (char) ('a' + toCol) + (8 - toRow));
                    field.switchTurn();
                    return;
                }
            }
        }
    }
}
