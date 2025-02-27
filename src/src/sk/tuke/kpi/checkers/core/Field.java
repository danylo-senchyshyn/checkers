package sk.tuke.kpi.checkers.core;


import sk.tuke.kpi.checkers.consoleui.ConsoleUI;

public class Field {
    private final int rows = 8;
    private final int cols = 8;
    private Tile[][] field;
    private ConsoleUI console;
    private boolean playerTurn = true;

    public Field() {
        field = new Tile[rows][cols];
        console = new ConsoleUI(this);
        createField();
    }

    public Tile[][] getField() {
        return field;
    }

    public ConsoleUI getConsole() {
        return console;
    }

    public void createField() {
        console.dialogWelcome();

        if (console.color.equals("w")) {
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
        } else if (console.color.equals("b")) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    if ((row + col) % 2 == 0) {
                        field[row][col] = new Tile(TileState.EMPTY);
                    } else if (row < 3) {
                        field[row][col] = new Man(TileState.WHITE_CHECKER);
                    } else if (row > 4) {
                        field[row][col] = new Man(TileState.BLACK_CHECKER);
                    } else {
                        field[row][col] = new Tile(TileState.EMPTY);
                    }
                }
            }
        }

        console.printBoard(field);
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

        if (fromTile instanceof Man man) {
            int rowDiff = toRow - fromRow;
            int colDiff = Math.abs(toCol - fromCol);
            int direction = (man.getState() == TileState.WHITE_CHECKER) ? -1 : 1;

            // Обычный ход
            if (rowDiff == direction && colDiff == 1) {
                return true;
            }

            // Ход с боем
            if (rowDiff == 2 * direction && colDiff == 2) {
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
            } else {
                return false;
            }
        }

        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile(TileState.EMPTY);

        if (toRow == 0 || toRow == 7) {
            field[toRow][toCol] = new King(field[toRow][toCol].getState());
        }

        console.printBoard(field);
        return true;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}
