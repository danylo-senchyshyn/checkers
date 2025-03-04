package sk.tuke.kpi.checkers;

import sk.tuke.kpi.checkers.consoleui.ConsoleUI;
import sk.tuke.kpi.checkers.core.Field;


public class Main {
    public static void main(String[] args) {
        Field field = new Field();
        ConsoleUI console = new ConsoleUI(field);
        console.play();
    }
}
