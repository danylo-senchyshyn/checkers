package sk.tuke.kpi.kp.game.checkers.consoleui;

public class ConsoleColor {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    // Print with color
    public String pwc(String text, String color) {
        return color + text + RESET;
    }
}
