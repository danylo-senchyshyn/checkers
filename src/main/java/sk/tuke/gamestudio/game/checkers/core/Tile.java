package sk.tuke.gamestudio.game.checkers.core;

public class Tile {
    private TileState tileState;

    public Tile (TileState tileState) {
        this.tileState = tileState;
    }

    public TileState getState() {
        return tileState;
    }

    public void setTileState(TileState tileState) {
        this.tileState = tileState;
    }

    public boolean isEmpty() {
        return tileState == TileState.EMPTY;
    }
    public boolean isNotEmpty() {
        return tileState != TileState.EMPTY;
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
            case EMPTY -> "\uD83D\uDFE7";
            case WHITE -> "⚪";
            case BLACK -> "⚫";
            case WHITE_KING -> "\uD83E\uDEC5\uD83C\uDFFB";
            case BLACK_KING -> "\uD83E\uDEC5\uD83C\uDFFF";
        };
    }
}
