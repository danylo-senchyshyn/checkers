package sk.tuke.gamestudio.game.checkers.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CheckersField {
    public static final int SIZE = 8;
    private static final int MAX_MOVES_WITHOUT_CAPTURE = 30;
    private static final int MAX_MOVES_BY_KINGS_ONLY = 15;

    @Getter
    private int movesWithoutCapture;
    @Getter
    private int movesByKingsOnly;

    @Getter
    private Tile[][] field;
    @Getter
    private boolean whiteTurn;
    @Getter
    @Setter
    private GameState gameState;
    @Getter
    @Setter
    private int scoreWhite;
    @Getter
    @Setter
    private int scoreBlack;

    public CheckersField() {
        field = new Tile[SIZE][SIZE];
        startNewGame();
    }

    public void startNewGame() {
        gameState = GameState.PLAYING;
        scoreWhite = scoreBlack = 0;
        whiteTurn = true;
        movesWithoutCapture = 0;
        movesByKingsOnly = 0;
        initializeField();
        //initializeTestField();
    }

    public void switchTurn() {
        whiteTurn = !whiteTurn;
    }

    // Creating the field
    public void initializeField() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    field[row][col] = new Tile(TileState.EMPTY_WHITE);
                } else {
                    field[row][col] = new Tile(TileState.EMPTY_BLACK);
                }

                if (row < 3 && (row + col) % 2 != 0) {
                    field[row][col] = new Man(TileState.BLACK);
                } else if (row > 4 && (row + col) % 2 != 0) {
                    field[row][col] = new Man(TileState.WHITE);
                }
            }
        }
    }
    private void initializeTestField() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field[i][j] = new Tile((i + j) % 2 == 0 ? TileState.EMPTY_WHITE : TileState.EMPTY_BLACK);
            }
        }

        field[1][1] = new Man(TileState.WHITE);
        field[6][6] = new Man(TileState.BLACK);
    }

    // Moving a checker
    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) return false;

        boolean isCapture = Math.abs(toRow - fromRow) == 2;
        if (isCapture) handleCapture(fromRow, fromCol, toRow, toCol);
        else movesWithoutCapture++;

        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile((fromRow + fromCol) % 2 == 0 ?
                TileState.EMPTY_WHITE
                :
                TileState.EMPTY_BLACK);

        checkKingPromotion(toRow, toCol);

        if (!isChecker(fromRow, fromCol))
            movesByKingsOnly++;

        switchTurn();

        updateGameState();
        return true;
    }

    private void handleCapture(int fromRow, int fromCol, int toRow, int toCol) {
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        field[midRow][midCol] = new Tile((midRow + midCol) % 2 == 0 ?
                TileState.EMPTY_WHITE
                :
                TileState.EMPTY_BLACK);

        movesWithoutCapture = 0;

        if (whiteTurn) scoreWhite += 3;
        else scoreBlack += 3;
    }

    // Validity check for the move
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (hasAnyCaptureMove()) {
            if (Math.abs(toRow - fromRow) != 2 && isChecker(fromRow, fromCol)) {
                System.out.println("You must capture a piece if possible!");
                return false;
            }
            if (!isChecker(fromRow, fromCol) && !isValidKingMove(fromRow, fromCol, toRow, toCol)) {
                System.out.println("The king must capture a piece!");
                return false;
            }
        }

        if (!isWithinBounds(fromRow, fromCol) || !isWithinBounds(toRow, toCol) ||
                field[fromRow][fromCol].isEmpty() || field[toRow][toCol].isNotEmpty() ||
                (isWhiteTurn() && field[fromRow][fromCol].getState().isBlack()) ||
                (!isWhiteTurn() && field[fromRow][fromCol].getState().isWhite())) {
            return false;
        }
        return isChecker(fromRow, fromCol) ?
                isValidManMove(fromRow, fromCol, toRow, toCol)
                :
                isValidKingMove(fromRow, fromCol, toRow, toCol);
    }

    // Regular Checkers
    private boolean isValidManMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDelta = toRow - fromRow;
        int colDelta = Math.abs(toCol - fromCol);
        int direction = (field[fromRow][fromCol].getState() == TileState.WHITE) ? -1 : 1;
        boolean is =  (rowDelta == direction && colDelta == 1) ||
                (Math.abs(rowDelta) == 2 && colDelta == 2 && isOpponentTile(field[(fromRow + toRow) / 2][(fromCol + toCol) / 2]));
        return is;
    }

    // Kings
    private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        return isPathClear(fromRow, fromCol, toRow, toCol);
    }

    // Check for an empty diagonal or presence of an opponent's checker
    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        for (int row = fromRow + rowStep, col = fromCol + colStep; row != toRow; row += rowStep, col += colStep) {
            if (field[row][col].isNotEmpty()) return false;
        }
        return true;
    }

    // Check for out-of-bounds
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    // King transformation
    private void checkKingPromotion(int row, int col) {
        TileState state = field[row][col].getState();
        if (row == 0 && state == TileState.WHITE) {
            field[row][col] = new King(TileState.WHITE_KING);
            setScoreWhite(getScoreWhite() + 5);
        }
        if (row == 7 && state == TileState.BLACK) {
            field[row][col] = new King(TileState.BLACK_KING);
            setScoreBlack(getScoreBlack() + 5);
        }
    }

    // Check for regular checker
    public boolean isChecker(int row, int col) {
        return field[row][col].getState() == TileState.WHITE || field[row][col].getState() == TileState.BLACK;
    }

    // Check for opponent
    private boolean isOpponentTile(Tile tile) {
        return (whiteTurn && tile.getState().isBlack()) || (!whiteTurn && tile.getState().isWhite());
    }

    // Check for end of game
    public void updateGameState() {
        boolean hasWhite = hasAny(TileState.WHITE, TileState.WHITE_KING);
        boolean hasBlack = hasAny(TileState.BLACK, TileState.BLACK_KING);

        if (!hasBlack) {
            gameState = GameState.WHITE_WON;
            scoreWhite += 30;
        } else if (!hasWhite) {
            gameState = GameState.BLACK_WON;
            scoreBlack += 30;
        } else if (movesWithoutCapture >= MAX_MOVES_WITHOUT_CAPTURE || movesByKingsOnly >= MAX_MOVES_BY_KINGS_ONLY) {
            gameState = GameState.DRAW;
            scoreWhite = scoreBlack += 10;
        }
    }

    // Universal method for checking the presence of checkers on the field
    private boolean hasAny(TileState man, TileState king) {
        return Arrays.stream(field)
                .flatMap(Arrays::stream)
                .anyMatch(tile -> tile.getState() == man || tile.getState() == king);
    }

    // Check for the presence of capture moves
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
}