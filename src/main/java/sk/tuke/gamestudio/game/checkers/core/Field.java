package sk.tuke.gamestudio.game.checkers.core;

public class Field {
    private static final int SIZE = 8;
    private final Tile[][] field;
    private boolean whiteTurn;
    private GameState gameState;
    private int movesWithoutCapture;
    private int movesByKingsOnly;
    private int scoreWhite;
    private int scoreBlack;

    public Field() {
        field = new Tile[SIZE][SIZE];
        createNewGame();
    }

    public void createNewGame() {
        gameState = GameState.PLAYING;
        scoreWhite = scoreBlack = 0;
        whiteTurn = true;
        movesWithoutCapture = 0;
        movesByKingsOnly = 0;
        createField();
        //createTestField();
    }

    public Tile[][] getField() {
        return field;
    }
    public boolean isWhiteTurn() {
        return whiteTurn;
    }
    public GameState getGameState() {
        return gameState;
    }
    public int getScoreWhite() {
        return scoreWhite;
    }
    public int getScoreBlack() {
        return scoreBlack;
    }

    public void switchTurn() {
        whiteTurn = !whiteTurn;
    }

    // Creating the field
    public void createField() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
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
    private void createTestField() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field[i][j] = new Tile(TileState.EMPTY);
            }
        }

        field[1][1] = new Man(TileState.WHITE);
        field[6][6] = new Man(TileState.BLACK);
    }

    // Moving a checker
    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        boolean isCapture = Math.abs(toRow - fromRow) == 2 && isPathClear(fromRow, fromCol, toRow, toCol, true);
        boolean isRegularCheckerMove = isChecker(fromRow, fromCol);

        if (hasAnyCaptureMove()) {
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

        if (isCapture) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            Tile midTile = field[midRow][midCol];

            if (isOpponentTile(midTile)) {
                field[midRow][midCol] = new Tile(TileState.EMPTY);
                movesWithoutCapture = 0;
            } else {
                return false;
            }

            if (whiteTurn) {
                scoreWhite += 3;
            } else {
                scoreBlack += 3;
            }
        } else {
            movesWithoutCapture++;
        }

        if (isRegularCheckerMove) {
            movesByKingsOnly = 0;
        } else {
            movesByKingsOnly++;
        }

        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile(TileState.EMPTY);

        checkKing(fromRow, fromCol, toRow, toCol);

        checkEndGame();

        return true;
    }

    // Validity check for the move
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isWithinBounds(fromRow, fromCol) || !isWithinBounds(toRow, toCol) ||
                field[fromRow][fromCol].isEmpty() || field[toRow][toCol].isNotEmpty() ||
                !checkValidTurn(fromRow, fromCol))
        {
            System.out.println("Error in isValidMove.");
            return false;
        }

        return isChecker(fromRow, fromCol) ?
                isValidManMove(fromRow, fromCol, toRow, toCol) :
                isValidKingMove(fromRow, fromCol, toRow, toCol);
    }

    private boolean checkValidTurn(int fromRow, int fromCol) {
        if (whiteTurn) {
            return field[fromRow][fromCol].getState() == TileState.WHITE || field[fromRow][fromCol].getState() == TileState.WHITE_KING;
        } else {
            return field[fromRow][fromCol].getState() == TileState.BLACK || field[fromRow][fromCol].getState() == TileState.BLACK_KING;
        }
    }

    // Regular Checkers
    private boolean isValidManMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDelta = toRow - fromRow;
        int colDelta = Math.abs(toCol - fromCol);
        int direction = (field[fromRow][fromCol].getState() == TileState.WHITE) ? -1 : 1;

        if (rowDelta == direction && colDelta == 1) {
            return true;
        } else if ((rowDelta == 2 * direction || rowDelta == 2 * -direction) && colDelta == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            return isOpponentTile(field[midRow][midCol]);
        }

        return false;
    }

    // Kings
    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        return isPathClear(fromRow, fromCol, toRow, toCol, false)
                || isPathClear(fromRow, fromCol, toRow, toCol, true);
    }

    // Check for an empty diagonal or presence of an opponent's checker
    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol, boolean checkForOpponent) {
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        int row = fromRow + rowStep;
        int col = fromCol + colStep;
        boolean foundOpponent = false;

        while (row != toRow && col != toCol) {
            if (field[row][col].isNotEmpty()) {
                if (checkForOpponent && isOpponentTile(field[row][col]) && !foundOpponent) {
                    foundOpponent = true;
                } else {
                    return false;
                }
            }
            row += rowStep;
            col += colStep;
        }

        return !checkForOpponent || foundOpponent;
    }

    // Check for out-of-bounds
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    // King transformation
    private void checkKing(int fromRow, int fromCol, int toRow, int toCol) {
        if (field[fromRow][fromCol].getState() == TileState.WHITE_KING || field[fromRow][fromCol].getState() == TileState.BLACK_KING) {
            return;
        }
        if (toRow == 0 && field[toRow][toCol].getState() == TileState.WHITE) {
            field[toRow][toCol] = new King(TileState.WHITE_KING);
            scoreWhite += 5;
        }
        if (toRow == 7 && field[toRow][toCol].getState() == TileState.BLACK) {
            field[toRow][toCol] = new King(TileState.BLACK_KING);
            scoreBlack += 5;
        }
    }

    // Check for opponent
    private boolean isOpponentTile(Tile tile) {
        return (whiteTurn && (tile.getState() == TileState.BLACK || tile.getState() == TileState.BLACK_KING)) ||
                (!whiteTurn && (tile.getState() == TileState.WHITE || tile.getState() == TileState.WHITE_KING));
    }

    // Check for regular checker
    private boolean isChecker(int row, int col) {
        return field[row][col].getState() == TileState.WHITE || field[row][col].getState() == TileState.BLACK;
    }

    // Check for King
    private boolean isKing(int row, int col) {
        return field[row][col].getState() == TileState.WHITE_KING || field[row][col].getState() == TileState.BLACK_KING;
    }

    // Check for the possibility of a capture move
    private boolean hasAnyCaptureMove() {
        int[][] directions = {{-1, 1}, {-1, -1}, {1, -1}, {1, 1}};

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = field[row][col];

                if (tile.isEmpty() || isOpponentTile(tile)) {
                    continue;
                }

                for (int[] dir : directions) {
                    int toRow = row + 2 * dir[0];
                    int toCol = col + 2 * dir[1];
                    int midRow = row + dir[0];
                    int midCol = col + dir[1];

                    if (isWithinBounds(toRow, toCol) && field[toRow][toCol].isEmpty()) {
                        if (isOpponentTile(field[midRow][midCol])) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // Check for end of game
    public void checkEndGame() {
        boolean hasWhite = hasAny(TileState.WHITE, TileState.WHITE_KING);
        boolean hasBlack = hasAny(TileState.BLACK, TileState.BLACK_KING);

        if (hasWhite && !hasBlack) {
            whiteWon();
        } else if (!hasWhite && hasBlack) {
            blackWon();
        } else if ((movesWithoutCapture >= 30 || movesByKingsOnly >= 15)) {
            draw();
        } else {
            gameState = GameState.PLAYING;
        }
    }

    public void whiteWon() {
        gameState = GameState.WHITE_WON;
        scoreWhite += 30;
    }
    public void blackWon() {
        gameState = GameState.BLACK_WON;
        scoreBlack += 30;
    }
    public void draw() {
        gameState = GameState.DRAW;
        scoreWhite = scoreBlack += 10;
    }

    // Universal method for checking the presence of checkers on the field
    private boolean hasAny(TileState man, TileState king) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (field[i][j].getState() == man || field[i][j].getState() == king) {
                    return true;
                }
            }
        }
        return false;
    }
}