package sk.tuke.kpi.kp.checkers.consoleui;

import sk.tuke.kpi.kp.checkers.core.Field;
import sk.tuke.kpi.kp.checkers.core.GameState;
import sk.tuke.kpi.kp.checkers.entity.Score;
import sk.tuke.kpi.kp.checkers.service.ScoreService;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private final Field field;
    private String nameWhitePlayer;
    private String nameBlackPlayer;
    private ScoreService scoreService;

    public ConsoleUI(Field field) {
        this.field = field;
    }

    public void play() throws InterruptedException {
        printWelcomeMessage();
        inputNames();

        while (field.getGameState() == GameState.PLAYING) {
            displayGameStats();
            printBoard();
            handleInput();
        }

        if (field.getGameState() != GameState.PLAYING) {
            saveScore();
            gameOver();
        }
    }

    private void printWelcomeMessage() {
        clean();

        System.out.println("===============================");
        System.out.println("   🎉 Welcome to Checkers! 🎉   ");
        System.out.println(" Enter 'exit' to end the game. ");
        System.out.println("===============================");

        input.nextLine();
    }

    private void inputNames() {
        clean();
        System.out.println("Enter name of player 1: ");
        nameWhitePlayer = input.nextLine();

        clean();
        System.out.println("Enter name of player 2: ");
        nameBlackPlayer = input.nextLine();

        clean();
    }

    private void printBoard() {
        String boardHeader = "    A   B   C   D   E   F   G   H ";
        String separator = "   -------------------------------";
        System.out.println(boardHeader);
        System.out.println(separator);

        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + "| ");
            for (int col = 0; col < 8; col++) {
                System.out.print(field.getField()[row][col] + "  ");
            }
            System.out.println("|" + (8 - row));
        }
        System.out.println(separator);
        System.out.println(boardHeader);
        System.out.println();
    }

    private void displayGameStats() {
        String gameStateMessage;
        switch (field.getGameState()) {
            case PLAYING -> gameStateMessage = "PLAYING";
            case WHITE_WON -> gameStateMessage = "White won!";
            case BLACK_WON -> gameStateMessage = "Black won!";
            case DRAW -> gameStateMessage = "Game ended in a draw!";
            default -> gameStateMessage = "";
        }
        System.out.println("Game state: " + gameStateMessage);

        System.out.printf("White score: %d\n", field.getScoreWhite());
        System.out.printf("Black score: %d\n", field.getScoreBlack());
    }

    private void handleInput() throws InterruptedException {
        System.out.println(field.isWhiteTurn() ? "White's turn." : "Black's turn.");
        System.out.println("Enter your move (e3 d4) or exit or print your scores: ");
        String move = input.nextLine().trim().toLowerCase();
        if (move.equals("exit")) {
            if (field.isWhiteTurn()) {
                field.whiteWon();
            } else {
                field.blackWon();
            }
            gameOver();
        } else if (move.equals("score")) {
            printScores();
        }

        if (!move.matches("^[a-h][1-8] [a-h][1-8]$")) {
            System.out.println("Invalid format! Please enter a move like: e3 d4\n");
            return;
        }

        String[] parts = move.split(" ");
        try {
            System.out.println();
            int fromRow = 8 - Character.getNumericValue(parts[0].charAt(1));
            int fromCol = parts[0].charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(parts[1].charAt(1));
            int toCol = parts[1].charAt(0) - 'a';

            if (field.move(fromRow, fromCol, toRow, toCol)) {
                field.switchTurn();
            } else {
                System.out.println("Invalid move.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gameOver() throws InterruptedException {
        String[] gameOverArt = {
                "  ██████╗  █████╗ ███╗   ███╗███████╗     ██████╗ ██╗   ██╗███████╗██████╗  ",
                " ██╔════╝ ██╔══██╗████╗ ████║██╔════╝     ██╔══██╗██║   ██║██╔════╝██╔══██╗ ",
                " ██║  ███╗███████║██╔████╔██║█████╗       ██║  ██║██║   ██║█████╗  ██████╔╝ ",
                " ██║   ██║██╔══██║██║╚██╔╝██║██╔══╝       ██║  ██║██║   ██║██╔══╝  ██╔══██╗ ",
                " ╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗     ██████╔╝╚██████╔╝███████╗██║  ██║ ",
                "  ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝ "
        };

        System.out.println();
        for (String line : gameOverArt) {
            System.out.println(line);
            Thread.sleep(200);
        }

        System.out.println("\n\nThanks for playing!");
    }

    public void saveScore() throws InterruptedException {
        scoreService.addScore(new Score("checkers", nameWhitePlayer, field.getScoreWhite(), new Date()));
        scoreService.addScore(new Score("checkers", nameBlackPlayer, field.getScoreBlack(), new Date()));
    }

    private void printScores() {
        List<Score> scores = scoreService.getTopScores("checkers");
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < scores.size(); i++) {
            var score = scores.get(i);
            System.out.printf("%d. %s %d\n", i + 1, score.getPlayer(), score.getPoints());
        }
        System.out.println("---------------------------------------------------------------");
    }

    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    public void clean() {
        for (int i = 0; i < 50; i++)
            System.out.println();
    }
}
