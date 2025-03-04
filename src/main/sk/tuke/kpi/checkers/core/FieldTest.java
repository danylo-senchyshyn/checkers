package sk.tuke.kpi.checkers.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class FieldTest {
    private Field field;

    @BeforeEach
    void setUp() {
        field = new Field();
    }

    @Test
    void testInitialSetup() {
        Tile[][] board = field.getField();

        // Проверяем начальную расстановку
        assertEquals(TileState.BLACK_CHECKER, board[0][1].getState());
        assertEquals(TileState.BLACK_CHECKER, board[2][1].getState());
        assertEquals(TileState.WHITE_CHECKER, board[5][0].getState());
        assertEquals(TileState.WHITE_CHECKER, board[7][1].getState());

        // Проверяем пустые клетки
        assertEquals(TileState.EMPTY, board[3][0].getState());
        assertEquals(TileState.EMPTY, board[4][1].getState());
    }

    @Test
    void testSwitchTurn() {
        assertTrue(field.isWhiteTurn());
        field.switchTurn();
        assertFalse(field.isWhiteTurn());
        field.switchTurn();
        assertTrue(field.isWhiteTurn());
    }

    @Test
    void testValidMove() {
        assertTrue(field.isValidMove(5, 0, 4, 1)); // Белая шашка делает корректный ход
        assertFalse(field.isValidMove(5, 0, 3, 2)); // Ход через две клетки без боя
        assertFalse(field.isValidMove(5, 0, 6, 1)); // Ход назад без превращения в дамку
    }

    @Test
    void testCaptureMove() {
        field.getField()[4][1] = new Man(TileState.BLACK_CHECKER); // Черная шашка перед белой
        assertTrue(field.isValidMove(5, 0, 3, 2)); // Белая бьет черную
        assertTrue(field.move(5, 0, 3, 2)); // Совершаем ход
        assertEquals(TileState.EMPTY, field.getField()[4][1].getState()); // Проверяем, что черная удалена
    }

    @Test
    void testKingPromotion() {
        field.getField()[1][2] = new Man(TileState.WHITE_CHECKER);
        assertTrue(field.move(1, 2, 0, 3)); // Двигаем белую шашку в конец
        assertTrue(field.getField()[0][3] instanceof King); // Проверяем, что стала дамкой
    }
}