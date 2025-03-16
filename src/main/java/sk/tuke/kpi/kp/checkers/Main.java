package sk.tuke.kpi.kp.checkers;

import sk.tuke.kpi.kp.checkers.consoleui.ConsoleUI;
import sk.tuke.kpi.kp.checkers.core.Field;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Field field = new Field();
        ConsoleUI console = new ConsoleUI(field);
        console.play();
    }
}
