package sk.tuke.gamestudio.game.checkers;

import sk.tuke.gamestudio.game.checkers.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.checkers.core.Field;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Field field = new Field();
        ConsoleUI console = new ConsoleUI(field);
        console.play();
    }
}
