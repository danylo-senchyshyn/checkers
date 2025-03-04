package sk.tuke.kpi.checkers.core;

public class Field {
    private final int rows = 8;
    private final int cols = 8;
    private Tile[][] field;
    public boolean whiteTurn;
    private boolean canContinueCapture;

    public Field() {
        field = new Tile[rows][cols];
        whiteTurn = true;
        canContinueCapture = false;
        createField();
    }

    public Tile[][] getField() {
        return field;
    }
    public boolean isWhiteTurn() {
        return whiteTurn;
    }
    public boolean canContinueCapture() {
        return canContinueCapture;
    }

    public void switchTurn() {
        whiteTurn = !whiteTurn;
        canContinueCapture = false;
    }

    public void createField() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if ((row + col) % 2 == 0) {
                    field[row][col] = new Tile(TileState.EMPTY);
                } else if (row < 3) {
                    field[row][col] = new Man(TileState.BLACK_CHECKER);
                } else if (row > 4) {
                    field[row][col] = new Man(TileState.WHITE_CHECKER);
                } else {
                    field[row][col] = new Tile(TileState.EMPTY);
                }
            }
        }
    }

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isWithinBounds(fromRow, fromCol) || !isWithinBounds(toRow, toCol)) {
            return false;
        }

        Tile fromTile = field[fromRow][fromCol];
        Tile toTile = field[toRow][toCol];

        if (fromTile.isEmpty() || !toTile.isEmpty()) {
            return false;
        }

        if (    (whiteTurn && fromTile.getState() != TileState.WHITE_CHECKER) ||
                (!whiteTurn && fromTile.getState() != TileState.BLACK_CHECKER)) {
            return false;
        }

        if (fromTile instanceof Man man) {
            int rowDiff = toRow - fromRow;
            int colDiff = Math.abs(toCol - fromCol);
            int direction = (man.getState() == TileState.WHITE_CHECKER) ? -1 : 1;

            // Обычный ход
            if (rowDiff == direction && colDiff == 1) {
                return true;
            }

            // Ход с боем
            while (rowDiff == 2 * direction && colDiff == 2) {
                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                Tile midTile = field[midRow][midCol];

                if (midTile instanceof Man && midTile.getState() != man.getState()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        if (Math.abs(toRow - fromRow) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            Tile midTile = field[midRow][midCol];

            if (midTile instanceof Man && midTile.getState() != field[fromRow][fromCol].getState()) {
                field[midRow][midCol] = new Tile(TileState.EMPTY);
                canContinueCapture = true;
            } else {
                return false;
            }
        }

        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile(TileState.EMPTY);

        if (toRow == 0 || toRow == 7) {
            field[toRow][toCol] = new King(field[toRow][toCol].getState());
        }

        return true;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}
