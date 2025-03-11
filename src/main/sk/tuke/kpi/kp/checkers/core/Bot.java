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
                                System.out.println("change canContinueCapture when capture move");
                                field.switchTurn();
                                return;
                            }
                        }
                    }
                }
            }
        }

        // Если нет захвата, сделать случайный ход
        while (!moveMade) {
            List<int[]> availableMoves = new ArrayList<>();
            for (int fromRow = 0; fromRow < 8; fromRow++) {
                for (int fromCol = 0; fromCol < 8; fromCol++) {
                    if (field.getField()[fromRow][fromCol].getState() == TileState.BLACK_CHECKER) {
                        int toRow = fromRow + 1;
                        int toCol = fromCol + (random.nextBoolean() ? 1 : -1);

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
