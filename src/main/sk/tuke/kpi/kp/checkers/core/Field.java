package sk.tuke.kpi.kp.checkers.core;

public class Field {
    private final int rows = 8;
    private final int cols = 8;
    private Tile[][] field;
    public boolean whiteTurn;

    public Field() {
        field = new Tile[rows][cols];
        whiteTurn = true;
        //createField();
        createTestFiled();
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
        }  else {
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

    public void createTestFiled() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field[i][j] = new Tile(TileState.EMPTY);
            }
        }
        field[5][4] = new Tile(TileState.BLACK);
        field[1][4] = new Man(TileState.WHITE);
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

        if ((whiteTurn && fromTile.getState() != TileState.WHITE && fromTile.getState() != TileState.WHITE_KING) ||
                (!whiteTurn && fromTile.getState() != TileState.BLACK && fromTile.getState() != TileState.BLACK_KING)) {
            return false;
        }

        if (fromTile instanceof Man man) {
            int rowDiff = toRow - fromRow;
            int colDiff = Math.abs(toCol - fromCol);
            int direction = (man.getState() == TileState.WHITE) ? -1 : 1;

            if (rowDiff == direction && colDiff == 1) {
                return true;
            }

            if (rowDiff == 2 * direction && colDiff == 2) {
                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                Tile midTile = field[midRow][midCol];

                if (fromTile.getState() == TileState.WHITE) {
                    if (isWithinBounds(midRow, midCol) && isWithinBounds(toRow, toCol) && (midTile.getState() == TileState.BLACK) && toTile.isEmpty()) {
                        return true;
                    }
                } else if (fromTile.getState() == TileState.BLACK) {
                    if (isWithinBounds(midRow, midCol) && isWithinBounds(toRow, toCol) && (midTile.getState() == TileState.WHITE) && toTile.isEmpty()) {
                        return true;
                    }
                }
            }
        } else if (fromTile instanceof King) {
            return isValidKingMove(fromRow, fromCol, toRow, toCol);
        }
        return false;
    }

    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff != colDiff) {
            return false;
        }

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

    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        if (Math.abs(toRow - fromRow) == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            Tile midTile = field[midRow][midCol];

            if (whiteTurn && (midTile.getState() == TileState.BLACK || midTile.getState() == TileState.BLACK_KING) && midTile.getState() != field[fromRow][fromCol].getState()) {
                field[midRow][midCol] = new Tile(TileState.EMPTY);
            } else if (!whiteTurn && (midTile.getState() == TileState.WHITE || midTile.getState() == TileState.WHITE_KING) && midTile.getState() != field[fromRow][fromCol].getState() && field[toRow][toCol].isEmpty()) {
                field[midRow][midCol] = new Tile(TileState.EMPTY);
            } else {
                return false;
            }
        } else if (field[fromRow][fromCol] instanceof King) {
            if (!isValidKingMove(fromRow, fromCol, toRow, toCol)) {
                return false;
            }
        }
        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile(TileState.EMPTY);

        checkKing(fromRow, fromCol, toRow, toCol);

        return true;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void checkKing(int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow == 0 && field[toRow][toCol].getState() == TileState.WHITE) {
            field[toRow][toCol] = new King(TileState.WHITE_KING);
        }
        if (toRow == 7 && field[toRow][toCol].getState() == TileState.BLACK) {
            field[toRow][toCol] = new King(TileState.BLACK_KING);
        }
    }

    public boolean endGame() {
        boolean whiteLeft = false;
        boolean blackLeft = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (field[i][j].getState() == TileState.WHITE || field[i][j].getState() == TileState.WHITE_KING) {
                    whiteLeft = true;
                } else if (field[i][j].getState() == TileState.BLACK || field[i][j].getState() == TileState.BLACK_KING) {
                    blackLeft = true;
                }
            }
        }

        if ((whiteLeft && !blackLeft) || (!whiteLeft && blackLeft)) {
            return true;
        } else {
            return false;
        }
    }
}
