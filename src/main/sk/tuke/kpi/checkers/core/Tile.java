package sk.tuke.kpi.checkers.core;

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

    @Override
    public String toString() {
        return switch (tileState) {
            case EMPTY -> "\uD83D\uDFE7";
            case WHITE_CHECKER -> "⚪";
            case BLACK_CHECKER -> "⚫";
            case WHITE_KING -> "\uD83E\uDEC5\uD83C\uDFFB";
            case BLACK_KING -> "\uD83E\uDEC5\uD83C\uDFFF";
        };
    }
}
