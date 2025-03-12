package sk.tuke.kpi.kp.checkers.core;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private final int rows = 8;
    private final int cols = 8;
    private Tile[][] field;
    public boolean whiteTurn;

    public Field() {
        field = new Tile[rows][cols];
        whiteTurn = true;
        //createField();
        createTestField();
    }

    public Tile[][] getField() {
        return field;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void switchTurn() {
        if (whiteTurn) {
            whiteTurn = false;
        } else {
            whiteTurn = true;
        }
    }

    public void createField() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if ((row + col) % 2 == 0) {
                    field[row][col] = new Tile(TileState.EMPTY);
                } else if (row < 3) {
                    field[row][col] = new Man(TileState.BLACK);
                } else if (row > 4) {
                    field[row][col] = new Man(TileState.WHITE);
                } else {
                    field[row][col] = new Tile(TileState.EMPTY);
                }
            }
        }
    }
    public void createTestField() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field[i][j] = new Tile(TileState.EMPTY);
            }
        }
        //field[3][4] = new Man(TileState.BLACK_KING);
        field[5][6] = new Man(TileState.WHITE);
        field[4][5] = new Man(TileState.BLACK);
        //field[6][1] = new Man(TileState.WHITE);
    }

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (hasCaptureMove(fromRow, fromCol)) {
            if (Math.abs(toRow - fromRow) != 2 && !isKing(fromRow, fromCol)) {
                System.out.println("You must capture a piece if possible!");
                return false;
            }
            if (!isChecker(fromRow, fromCol) && !isValidKingMove(fromRow, fromCol, toRow, toCol)) {
                System.out.println("The king must capture a piece!");
                return false;
            }
        }

        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        if (Math.abs(toRow - fromRow) == 2) {
            if (isChecker(fromRow, fromCol)) {
                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                Tile midTile = field[midRow][midCol];

                if (isOpponentTile(midTile)) {
                    field[midRow][midCol] = new Tile(TileState.EMPTY);
                } else {
                    return false;
                }
            }
        }

        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile(TileState.EMPTY);

        checkKing(fromRow, fromCol, toRow, toCol);

        return true;
    }

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        Tile fromTile = field[fromRow][fromCol];
        Tile toTile = field[toRow][toCol];

        if (
                !isWithinBounds(fromRow, fromCol) || !isWithinBounds(toRow, toCol) ||
                fromTile.isEmpty() || toTile.isNotEmpty()
        ) {
            System.out.println("Error in isValidMove.");
            return false;
        }

        if (isChecker(fromRow, fromCol)) {
            return isValidManMove(fromRow, fromCol, toRow, toCol);
        }
        else if (isKing(fromRow, fromCol)) {
            return isValidKingMove(fromRow, fromCol, toRow, toCol);
        }

        return false;
    }

    // Обычные Шашки
    public boolean isValidManMove(int fromRow, int fromCol, int toRow, int toCol) {
        // 5 0
        // 4 1
        int rowDelta = toRow - fromRow;
        int colDelta = Math.abs(toCol - fromCol);
        Tile fromTile = field[fromRow][fromCol];
        int direction = (fromTile.getState() == TileState.WHITE) ? -1 : 1;

        // Обычный ход
        if (rowDelta == direction && colDelta == 1) {
            return true;
        }
        // Ход со взятием
        else if (rowDelta == 2 * direction && colDelta == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            Tile midTile = field[midRow][midCol];

            if (isOpponentTile(midTile)) {
                return true;
            }
        }

        return false;
    }

    // Дамки
    public boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (isCleanDiagonal(fromRow, fromCol, toRow, toCol)) {
            return true;
        }
        else if (isCheckerOnDiagonal(fromRow, fromCol, toRow, toCol)) {
            cleanDiagol(fromRow, fromCol, toRow, toCol);
            return true;
        }

        return false;
    }

    public boolean isCleanDiagonal(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        int row = fromRow + rowStep;
        int col = fromCol + colStep;

        while (row != toRow && col != toCol) {
            if (field[row][col].isNotEmpty()) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }

        return true;
    }

    public boolean isCheckerOnDiagonal(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        int row = fromRow + rowStep;
        int col = fromCol + colStep;

        List<Tile> tiles = new ArrayList<>();
        while (row != toRow && col != toCol) {
            if (field[row][col].isNotEmpty()) {
                tiles.add(field[row][col]);
            }
            row += rowStep;
            col += colStep;
        }

        if (tiles.size() == 1 && isOpponentTile(tiles.get(0))) {
            return true;
        }

        return false;
    }

    public void cleanDiagol(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        int row = fromRow + rowStep;
        int col = fromCol + colStep;

        while (row != toRow && col != toCol) {
            if (isOpponentTile(field[row][col])) {
                field[row][col] = new Tile(TileState.EMPTY);
            }
            row += rowStep;
            col += colStep;
        }
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    public void checkKing(int fromRow, int fromCol, int toRow, int toCol) {
        if (field[fromRow][fromCol].getState() == TileState.WHITE_KING || field[fromRow][fromCol].getState() == TileState.BLACK_KING) {
            return;
        }
        if (toRow == 0 && field[toRow][toCol].getState() == TileState.WHITE) {
            field[toRow][toCol] = new King(TileState.WHITE_KING);
        }
        if (toRow == 7 && field[toRow][toCol].getState() == TileState.BLACK) {
            field[toRow][toCol] = new King(TileState.BLACK_KING);
        }
    }
    public boolean isOpponentTile(Tile tile) {
        if (whiteTurn) {
            return tile.getState() == TileState.BLACK || tile.getState() == TileState.BLACK_KING;
        } else {
            return tile.getState() == TileState.WHITE || tile.getState() == TileState.WHITE_KING;
        }
    }
    public boolean isChecker(int row, int col) {
        return field[row][col].getState() == TileState.WHITE || field[row][col].getState() == TileState.BLACK;
    }
    public boolean isKing(int row, int col) {
        return field[row][col].getState() == TileState.WHITE_KING || field[row][col].getState() == TileState.BLACK_KING;
    }
    public boolean hasCaptureMove(int fromRow, int fromCol) {
        int[][] directions = {{-1, 1}, {-1, -1}, {1, -1}, {1, 1}};
        for (int[] dir : directions) {
            int toRow = fromRow + dir[0];
            int toCol = fromCol + dir[1];
            if (isWithinBounds(toRow, toCol) && isOpponentTile(field[toRow][toCol])) {
                return true;
            }
        }
        return false;
    }

    public boolean endGame() {
        boolean whiteLeft = false;
        boolean blackLeft = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                TileState state = field[i][j].getState();
                if (state == TileState.WHITE || state == TileState.WHITE_KING) {
                    whiteLeft = true;
                } else if (state == TileState.BLACK || state == TileState.BLACK_KING) {
                    blackLeft = true;
                }

                if (whiteLeft && blackLeft) {
                    return false;
                }
            }
        }
        return true;
    }
}