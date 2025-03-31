package sk.tuke.gamestudio.game.checkers;

import sk.tuke.gamestudio.game.checkers.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.checkers.core.CheckersField;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CheckersField field = new CheckersField();
        ConsoleUI console = new ConsoleUI(field);
        console.play();
    }
}
