package sk.tuke.kpi.checkers.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    @Test
    public void testFieldInitialization() {
        Field field = new Field();
        Tile[][] tiles = field.getField();
        assertNotNull(tiles, "Field initialization failed - tiles array is null.");

        int blackCheckersCount = 0;
        int whiteCheckersCount = 0;

        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles[row].length; col++) {
                Tile tile = tiles[row][col];
                if (tile.getState() == TileState.BLACK_CHECKER) {
                    blackCheckersCount++;
                } else if (tile.getState() == TileState.WHITE_CHECKER) {
                    whiteCheckersCount++;
                }
            }
        }

        assertEquals(12, blackCheckersCount);
        assertEquals(12, whiteCheckersCount);
    }

    @Test
    public void testIsValidMove() {
        Field field = new Field();
        assertTrue(field.isValidMove(5, 4, 4, 3));
        assertFalse(field.isValidMove(0, 0, 2, 2));
    }

    @Test
    public void testMove() {
        Field field = new Field();
        assertTrue(field.move(5, 0, 4, 1));
        assertFalse(field.move(0, 0, 2, 2));
    }

    @Test
    public void testSwitchTurn() {
        Field field = new Field();
        boolean initialTurn = field.isWhiteTurn();
        field.switchTurn();
        assertNotEquals(initialTurn, field.isWhiteTurn());
    }
}
