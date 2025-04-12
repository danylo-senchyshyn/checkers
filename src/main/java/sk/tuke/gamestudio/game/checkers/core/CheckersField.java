package sk.tuke.gamestudio.game.checkers.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Setter
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
        //initializeField();
        initializeTestField();
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
    public void initializeTestField() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field[i][j] = new Tile((i + j) % 2 == 0 ? TileState.EMPTY_WHITE : TileState.EMPTY_BLACK);
            }
        }

        field[1][3] = new Man(TileState.WHITE_KING);
        field[6][6] = new Man(TileState.BLACK_KING);
        field[3][2] = new Tile(TileState.WHITE);
    }

    // Moving a checker
    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidMove(fromRow, fromCol, toRow, toCol)) return false;

        boolean isCapture = Math.abs(toRow - fromRow) == 2;
        if (!isChecker(fromRow, fromCol)) {
            handleKingCapture(fromRow, fromCol, toRow, toCol);
            movesByKingsOnly++;
        } else if (isCapture) {
            handleCapture(fromRow, fromCol, toRow, toCol);
        } else {
            movesWithoutCapture++;
        }

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

    private void handleKingCapture(int fromRow, int fromCol, int toRow, int toCol) {
        int stepRow = (toRow - fromRow) > 0 ? 1 : -1;
        int stepCol = (toCol - fromCol) > 0 ? 1 : -1;

        int currentRow = fromRow + stepRow;
        int currentCol = fromCol + stepCol;

        while (currentRow != toRow && currentCol != toCol) {
            Tile tile = field[currentRow][currentCol];

            if (tile.isNotEmpty() && isOpponentTile(tile)) {
                field[currentRow][currentCol] = new Tile((currentRow + currentCol) % 2 == 0
                        ? TileState.EMPTY_WHITE
                        : TileState.EMPTY_BLACK);
                movesWithoutCapture = 0;
                if (whiteTurn) scoreWhite += 3;
                else scoreBlack += 3;
                break; // только одну шашку можно бить за раз
            }

            currentRow += stepRow;
            currentCol += stepCol;
        }
    }

    // Validity check for the move
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (hasAnyCaptureMove()) {
            if (isChecker(fromRow, fromCol)) {
                if (!(Math.abs(toRow - fromRow) == 2 && Math.abs(toCol - fromCol) == 2 &&
                        isOpponentTile(field[(fromRow + toRow) / 2][(fromCol + toCol) / 2]))) {
                    System.out.println("You must capture a piece if possible!");
                    return false;
                }
            } else {
                if (!isValidKingCapture(fromRow, fromCol, toRow, toCol)) {
                    System.out.println("The king must capture a piece!");
                    return false;
                }
            }
        }

        if (!isWithinBounds(fromRow, fromCol) || !isWithinBounds(toRow, toCol) ||
                field[fromRow][fromCol].isEmpty() || field[toRow][toCol].isNotEmpty() ||
                (isWhiteTurn() && field[fromRow][fromCol].getState().isBlack()) ||
                (!isWhiteTurn() && field[fromRow][fromCol].getState().isWhite())){
            return false;
        }

        System.out.println(isValidKingMove(fromRow, fromCol, toRow, toCol));

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
        // Проверка на диагональ
        if (Math.abs(toRow - fromRow) != Math.abs(toCol - fromCol)) return false;

        int stepRow = Integer.signum(toRow - fromRow);
        int stepCol = Integer.signum(toCol - fromCol);

        int currentRow = fromRow + stepRow;
        int currentCol = fromCol + stepCol;

        while (currentRow != toRow && currentCol != toCol) {
            if (field[currentRow][currentCol].isNotEmpty() && !isOpponentTile(field[currentRow][currentCol])) {
                return false; // Обычный ход — только по пустым клеткам
            }
            currentRow += stepRow;
            currentCol += stepCol;
        }

        return true;
    }
    private boolean isValidKingCapture(int fromRow, int fromCol, int toRow, int toCol) {
        // Диагональ
        if (Math.abs(toRow - fromRow) != Math.abs(toCol - fromCol)) return false;

        int stepRow = Integer.signum(toRow - fromRow);
        int stepCol = Integer.signum(toCol - fromCol);

        int currentRow = fromRow + stepRow;
        int currentCol = fromCol + stepCol;
        int opponentFound = 0;

        while (currentRow != toRow && currentCol != toCol) {
            Tile currTile = field[currentRow][currentCol];
            if (currTile.isNotEmpty()) {
                if (!isOpponentTile(currTile) || opponentFound > 0) return false;
                opponentFound++;
            }
            currentRow += stepRow;
            currentCol += stepCol;
        }

        return opponentFound == 1;
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

    public boolean hasAnyCaptureMove() {
        return hasAnyManCaptureMove() || hasAnyKingCaptureMove();
    }

    // Check for the presence of capture moves
    private boolean hasAnyManCaptureMove() {
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

    private boolean hasAnyKingCaptureMove() {
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = field[row][col];

                if (tile.isEmpty() || !tile.isKing() || isOpponentTile(tile)) {
                    continue; // Пропускаем пустые клетки, не-короли и шашки противника
                }

                for (int[] dir : directions) {
                    int stepRow = dir[0], stepCol = dir[1];
                    int r = row + stepRow;
                    int c = col + stepCol;
                    boolean foundOpponent = false;

                    while (isWithinBounds(r, c)) {
                        Tile t = field[r][c];

                        if (t.isNotEmpty()) {
                            if (isOpponentTile(t) && !foundOpponent) {
                                foundOpponent = true; // Найдена шашка противника
                            } else {
                                break; // Если встретили свою фигуру или вторую шашку
                            }
                        } else {
                            if (foundOpponent) {
                                return true; // Если за шашкой противника есть пустая клетка
                            }
                        }

                        r += stepRow;
                        c += stepCol;
                    }
                }
            }
        }

        return false; // Если ни один ход не найден
    }

    public List<int[]> getPossibleMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();

        Tile tile = field[row][col];
        if (tile.isEmpty() || (isWhiteTurn() && tile.isBlack()) || (!isWhiteTurn() && tile.isWhite())) {
            return moves;
        }

        if (tile.isChecker()) {
            int direction = tile.isWhite() ? -1 : 1;
            addMoveIfValid(moves, row + direction, col - 1);
            addMoveIfValid(moves, row + direction, col + 1);

            addCaptureMoveIfValid(moves, row + 2 * direction, col - 2, row + direction, col - 1);
            addCaptureMoveIfValid(moves, row + 2 * direction, col + 2, row + direction, col + 1);
        }

        if (tile.isKing()) {
            int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] dir : directions) {
                int r = row + dir[0];
                int c = col + dir[1];
                while (isWithinBounds(r, c) && field[r][c].isEmpty()) {
                    moves.add(new int[]{r, c});
                    r += dir[0];
                    c += dir[1];
                }

                if (isWithinBounds(r, c) && isOpponentTile(field[r][c])) {
                    int captureRow = r + dir[0];
                    int captureCol = c + dir[1];
                    if (isWithinBounds(captureRow, captureCol) && field[captureRow][captureCol].isEmpty()) {
                        moves.add(new int[]{captureRow, captureCol});
                    }
                }
            }
        }
        System.out.println("Возможные ходы для клетки (" + row + ", " + col + "): " + moves);
        return moves;
    }
    private void addMoveIfValid(List<int[]> moves, int row, int col) {
        if (isWithinBounds(row, col) && field[row][col].isEmpty()) {
            moves.add(new int[]{row, col});
        }
    }
    private void addCaptureMoveIfValid(List<int[]> moves, int toRow, int toCol, int captureRow, int captureCol) {
        if (isWithinBounds(toRow, toCol) && isWithinBounds(captureRow, captureCol)
                && isOpponentTile(field[captureRow][captureCol])
                && field[toRow][toCol].isEmpty()) {
            moves.add(new int[]{toRow, toCol});
        }
    }

    public void printField() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = field[row][col];
                System.out.print(tile.toString() + " ");
            }
            System.out.println(); // Переход на новую строку после каждой строки поля
        }
        System.out.println(); // Пустая строка для разделения
    }
}