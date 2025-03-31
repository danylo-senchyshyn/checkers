package sk.tuke.gamestudio.game.checkers.core;

public enum TileState {
    BLACK,
    WHITE,

    BLACK_KING,
    WHITE_KING,

    EMPTY;

    public boolean isBlack() {
        return this == BLACK || this == BLACK_KING;
    }

    public boolean isWhite() {
        return this == WHITE || this == WHITE_KING;
    }
}