package sk.tuke.kpi.kp.checkers.core;

public class Field {
    private final int rows = 8;
    private final int cols = 8;
    private Tile[][] field;
    public boolean whiteTurn;

    public Field() {
        field = new Tile[rows][cols];
        whiteTurn = true;
        createField();
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

        if ((whiteTurn && fromTile.getState() != TileState.WHITE_CHECKER) ||
                (!whiteTurn && fromTile.getState() != TileState.BLACK_CHECKER)) {
            return false;
        }

        if (fromTile instanceof Man man) {
            int rowDiff = toRow - fromRow;
            int colDiff = Math.abs(toCol - fromCol);
            int direction = (man.getState() == TileState.WHITE_CHECKER) ? -1 : 1;

            if (rowDiff == direction && colDiff == 1) {
                return true;
            }

            if (rowDiff == 2 * direction && colDiff == 2) {
                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                Tile midTile = field[midRow][midCol];

                if (fromTile.getState() == TileState.WHITE_CHECKER) {
                    if (isWithinBounds(midRow, midCol) && isWithinBounds(toRow, toCol) && (midTile.getState() == TileState.BLACK_CHECKER) && toTile.isEmpty()) {
                        return true;
                    }
                } else if (fromTile.getState() == TileState.BLACK_CHECKER) {
                    if (isWithinBounds(midRow, midCol) && isWithinBounds(toRow, toCol) && (midTile.getState() == TileState.WHITE_CHECKER) && toTile.isEmpty()) {
                        return true;
                    }
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

            if (whiteTurn && (midTile.getState() == TileState.BLACK_CHECKER || midTile.getState() == TileState.BLACK_KING) && midTile.getState() != field[fromRow][fromCol].getState()) {
                field[midRow][midCol] = new Tile(TileState.EMPTY);
            } else if (!whiteTurn && (midTile.getState() == TileState.WHITE_CHECKER || midTile.getState() == TileState.WHITE_KING) && midTile.getState() != field[fromRow][fromCol].getState() && field[toRow][toCol].isEmpty()) {
                field[midRow][midCol] = new Tile(TileState.EMPTY);
            } else {
                return false;
            }
        }

        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile(TileState.EMPTY);

        if (toRow == 0 && field[toRow][toCol].getState() == TileState.WHITE_CHECKER) {
            field[toRow][toCol] = new King(TileState.WHITE_KING);
        }
        if (toRow == 7 && field[toRow][toCol].getState() == TileState.BLACK_CHECKER) {
            field[toRow][toCol] = new King(TileState.BLACK_KING);
        }

        return true;
    }


    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean endGame() {
        boolean whiteLeft = false;
        boolean blackLeft = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (field[i][j].getState() == TileState.WHITE_CHECKER || field[i][j].getState() == TileState.WHITE_KING) {
                    whiteLeft = true;
                } else if (field[i][j].getState() == TileState.BLACK_CHECKER || field[i][j].getState() == TileState.BLACK_KING) {
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
