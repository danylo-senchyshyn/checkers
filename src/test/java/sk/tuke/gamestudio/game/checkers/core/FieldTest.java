package sk.tuke.gamestudio.game.checkers.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private CheckersField field;

    public FieldTest() {
        this.field = new CheckersField();
    }

    @Test
    public void testFieldInitialization() {
        field.startNewGame();

        Tile[][] tiles = field.getField();
        assertNotNull(tiles);

        int blackCheckersCount = 0;
        int whiteCheckersCount = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Tile tile = tiles[row][col];
                if (tile.getState() == TileState.BLACK) {
                    blackCheckersCount++;
                } else if (tile.getState() == TileState.WHITE) {
                    whiteCheckersCount++;
                }
            }
        }

        assertEquals(12, blackCheckersCount);
        assertEquals(12, whiteCheckersCount);
    }

    @Test
    public void testIsValidMove() {
        field.startNewGame();

        assertTrue(field.isValidMove(5, 4, 4, 3));
        assertFalse(field.isValidMove(0, 0, 2, 2));
    }

    @Test
    public void testMove() {
        field.startNewGame();

        assertTrue(field.move(5, 0, 4, 1));
        assertFalse(field.move(0, 0, 2, 2));
    }

    @Test
    public void testSwitchTurn() {
        boolean initialTurn = field.isWhiteTurn();
        field.switchTurn();
        assertNotEquals(initialTurn, field.isWhiteTurn());
    }

    @Test
    public void testWhiteKingPromotion() {
        getEmptyField();

        field.getField()[1][0] = new Man(TileState.WHITE);
        assertTrue(field.move(1, 0, 0, 1));
        assertTrue(field.getField()[0][1] instanceof King);
    }

    @Test
    public void testBlackKingPromotion() {
        getEmptyField();

        field.switchTurn();
        field.getField()[6][1] = new Man(TileState.BLACK);

        assertTrue(field.move(6, 1, 7, 0));
        assertTrue(field.getField()[7][0] instanceof King);
    }

    @Test
    public void testCaptureMove() {
        getEmptyField();

        field.getField()[4][4] = new Man(TileState.WHITE);
        field.getField()[3][5] = new Man(TileState.BLACK);

        assertTrue(field.move(4, 4, 2, 6));
        assertTrue(field.getField()[2][6] instanceof Man && field.getField()[2][6].getState() == TileState.WHITE);
        assertTrue(field.getField()[5][5].isEmpty());
    }

    @Test
    public void testEndGame() {
        getEmptyField();

        field.getField()[0][0] = new Man(TileState.WHITE);
        field.updateGameState();
        assertEquals(field.getGameState(), GameState.WHITE_WON);

        getEmptyField();
        field.getField()[0][0] = new Man(TileState.BLACK);
        field.updateGameState();
        assertEquals(field.getGameState(), GameState.BLACK_WON);
    }

    private void getEmptyField() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                field.getField()[row][col] = new Tile(TileState.EMPTY_WHITE);
            }
        }
    }
}
