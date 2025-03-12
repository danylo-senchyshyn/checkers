package sk.tuke.kpi.kp.checkers.test;

import org.junit.jupiter.api.Test;
import sk.tuke.kpi.kp.checkers.core.*;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private Field field;

    public FieldTest() {
        this.field = new Field();
    }

    @Test
    public void testFieldInitialization() {
        Tile[][] tiles = field.getField();
        assertNotNull(tiles, "Field initialization failed - tiles array is null.");

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
        assertTrue(field.isValidMove(5, 4, 4, 3));
        assertFalse(field.isValidMove(0, 0, 2, 2));
    }

    @Test
    public void testMove() {
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
        assertTrue(field.move(1, 0, 0, 1), "White checker move failed.");
        assertTrue(field.getField()[0][1] instanceof King, "White checker was not promoted to King.");
    }

    @Test
    public void testBlackKingPromotion() {
        getEmptyField();

        field.switchTurn(); // Switch to black's turn
        field.getField()[6][1] = new Man(TileState.BLACK);

        assertTrue(field.move(6, 1, 7, 0), "Black checker move failed.");
        assertTrue(field.getField()[7][0] instanceof King, "Black checker was not promoted to King.");
    }

    @Test
    public void testCaptureMove() {
        getEmptyField();

        field.getField()[4][4] = new Man(TileState.WHITE);
        field.getField()[3][5] = new Man(TileState.BLACK);

        assertTrue(field.move(4, 4, 2, 6), "Capture move failed.");
        assertTrue(field.getField()[2][6] instanceof Man && field.getField()[2][6].getState() == TileState.WHITE, "Capture move did not place the capturing checker correctly.");
        assertTrue(field.getField()[5][5].isEmpty(), "Capture move did not clear the captured checker.");
    }

    @Test
    public void testMultipleCaptureMove() {
        getEmptyField();

        field.getField()[6][0] = new Man(TileState.WHITE);
        field.getField()[5][1] = new Man(TileState.BLACK);
        field.getField()[3][3] = new Man(TileState.BLACK);

        assertTrue(field.move(6, 0, 4, 2), "First capture move failed.");
        assertTrue(field.move(4, 2, 2, 4), "Second capture move failed.");
        assertTrue(field.getField()[2][4] instanceof Man && field.getField()[2][4].getState() == TileState.WHITE, "Multiple capture move did not place the capturing checker correctly.");
        assertTrue(field.getField()[3][3].isEmpty(), "First captured checker was not cleared.");
        assertTrue(field.getField()[5][5].isEmpty(), "Second captured checker was not cleared.");
    }

    @Test
    public void testEndGame() {
        getEmptyField();
        field.getField()[0][0] = new Man(TileState.WHITE);
        assertTrue(field.endGame(), "End game failed when there are no black checkers left.");

        getEmptyField();
        field.getField()[0][0] = new Man(TileState.BLACK);
        assertTrue(field.endGame(), "End game failed when there are no white checkers left.");

        getEmptyField();
        field.getField()[0][0] = new Man(TileState.WHITE);
        field.getField()[7][7] = new Man(TileState.BLACK);
        assertFalse(field.endGame(), "End game incorrectly detected when both colors are present.");
    }

    /*
    @Test
    public void testBotRegularMove() {
        getEmptyField();
        field.whiteTurn = false;
        field.getField()[2][7] = new Man(TileState.BLACK);
        bot.makeMove(field);

        assertTrue(bot.moveMade);
        assertTrue(field.getField()[3][6] instanceof Man && field.getField()[3][6].getState() == TileState.BLACK);
    }

    @Test
    public void testBotCaptureMove() {
        getEmptyField();
        field.whiteTurn = false;
        field.getField()[3][2] = new Man(TileState.BLACK);
        field.getField()[4][3] = new Man(TileState.WHITE);
        bot.makeMove(field);

        assertTrue(bot.moveMade);
        assertTrue(field.getField()[4][3].isEmpty());
        assertTrue(field.getField()[5][4].getState() == TileState.BLACK);
    }
    */


    private void getEmptyField() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                field.getField()[row][col] = new Tile(TileState.EMPTY);
            }
        }
    }
}
