package sk.tuke.kpi.kp.game.checkers;

import sk.tuke.kpi.kp.game.checkers.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.game.checkers.core.Field;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Field field = new Field();
        ConsoleUI console = new ConsoleUI(field);
        console.play();
    }
}
