package sk.tuke.gamestudio.game.checkers.core;

public class Tile {
    private TileState tileState;
    private Tile[][] field;

    public Tile (TileState tileState) {
        this.tileState = tileState;
    }

    public TileState getState() {
        return tileState;
    }
    public Tile getTile(int row, int col) {
        return field[row][col];
    }
    public String getStyle() {
        return switch (tileState) {
            case EMPTY_WHITE -> "empty-white";
            case EMPTY_BLACK -> "empty-black";
            case WHITE -> "white";
            case BLACK -> "black";
            case WHITE_KING -> "white-king";
            case BLACK_KING -> "black-king";
        };
    }


    public boolean isEmpty() {
        return tileState == TileState.EMPTY_WHITE || tileState == TileState.EMPTY_BLACK;
    }
    public boolean isNotEmpty() {
        return !isEmpty();
    }
    public boolean isWhite() {
        return tileState == TileState.WHITE || tileState == TileState.WHITE_KING;
    }
    public boolean isBlack() {
        return tileState == TileState.BLACK || tileState == TileState.BLACK_KING;
    }

    @Override
    public String toString() {
        return switch (tileState) {
            case EMPTY_WHITE -> "\uD83D\uDFE7";
            case EMPTY_BLACK -> "\uD83D\uDFE7";
            case WHITE -> "⚪";
            case BLACK -> "⚫";
            case WHITE_KING -> "\uD83E\uDEC5\uD83C\uDFFB";
            case BLACK_KING -> "\uD83E\uDEC5\uD83C\uDFFF";
        };
    }
}
