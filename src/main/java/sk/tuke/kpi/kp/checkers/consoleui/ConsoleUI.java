package sk.tuke.kpi.kp.checkers.consoleui;

import sk.tuke.kpi.kp.checkers.core.Field;
import sk.tuke.kpi.kp.checkers.core.GameState;
import sk.tuke.kpi.kp.checkers.entity.Comment;
import sk.tuke.kpi.kp.checkers.entity.Rating;
import sk.tuke.kpi.kp.checkers.entity.Score;
import sk.tuke.kpi.kp.checkers.service.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private final Field field;
    private String nameWhitePlayer;
    private String nameBlackPlayer;
    private ScoreService scoreService;
    private RatingService ratingService;
    private CommentService commentService;
    private final ScoreServiceJDBC scoreServiceJDBC;
    private final CommentServiceJDBC commentServiceJDBC;
    private final RatingServiceJDBC ratingServiceJDBC;

    public ConsoleUI(Field field) {
        this.field = field;
        scoreServiceJDBC = new ScoreServiceJDBC();
        commentServiceJDBC = new CommentServiceJDBC();
        ratingServiceJDBC = new RatingServiceJDBC();

        this.scoreService = scoreServiceJDBC;
        this.commentService = commentServiceJDBC;
        this.ratingService = ratingServiceJDBC;
    }

    // Play the game
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
            printStatsAfterGame();
        }
    }

    // Print welcome message
    private void printWelcomeMessage() {
        System.out.println("===============================");
        System.out.println("   🎉 Welcome to Checkers! 🎉   ");
        System.out.println(" Enter 'exit' to end the game. ");
        System.out.println("===============================");

        input.nextLine();
    }

    // Input player names
    private void inputNames() {
        System.out.println("Enter name of player 1: ");
        nameWhitePlayer = input.nextLine();
        System.out.println("Enter name of player 2: ");
        nameBlackPlayer = input.nextLine();

        System.out.println("\n🎮 Game started! Let's play!\n");
    }

    // Print board
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

    // Display game stats
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

    // Handle user input
    private void handleInput() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println(field.isWhiteTurn() ? "⚪ White's turn." : "⚫ Black's turn.");
        System.out.println("Enter your move (e3 d4), or use: ");
        System.out.println("  🏁  'e'  - Exit game");
        System.out.println("  ⚖   'd'  - Declare draw");
        System.out.println("  🏆  'ss' - Show top scores");
        System.out.println("  💬  'sc' - Show comments");
        System.out.println("  📊  'sr' - Show average rating");
        System.out.println("  🔄  'rs' - Reset scores");
        System.out.println("  🔄  'rc' - Reset comments");
        System.out.println("  ✍️  'ac' - Add comment");
        System.out.println("  ⭐  'ad' - Add rating");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        System.out.print("🔹 Your input: ");
        String inputStr = input.nextLine().trim().toLowerCase();

        switch (inputStr) {
            case "e" -> {
                System.out.println("\n🏁 Exiting the game...");
                if (field.isWhiteTurn()) {
                    field.blackWon();
                } else {
                    field.whiteWon();
                }
            }
            case "d" -> {
                System.out.println("\n⚖ Game ended in a draw.");
                field.draw();
            }
            case "ss" -> printScores();
            case "rs" -> scoreService.reset();
            case "rc" -> commentService.reset();
            case "ac" -> addCom();
            case "sc" -> printComs();
            case "ad" -> addRat();
            case "sr" -> getAvgRating();
            default -> processMove(inputStr);
        }
    }
    // Process move
    private void processMove(String inputStr) {
        if (!inputStr.matches("^[a-h][1-8] [a-h][1-8]$")) {
            System.out.println("❌ Invalid format! Please enter a move like: e3 d4\n");
            return;
        }

        String[] parts = inputStr.split(" ");
        try {
            System.out.println();
            int fromRow = 8 - Character.getNumericValue(parts[0].charAt(1));
            int fromCol = parts[0].charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(parts[1].charAt(1));
            int toCol = parts[1].charAt(0) - 'a';

            if (field.move(fromRow, fromCol, toRow, toCol)) {
                field.switchTurn();
            } else {
                System.out.println("❌ Invalid move.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Game over
    public void gameOver() throws InterruptedException {
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

    public void printStatsAfterGame() {
        String playerName = field.isWhiteTurn() ? nameWhitePlayer : nameBlackPlayer;

        double avgRating = ratingService.getAverageRating("checkers");
        int rat = ratingService.getRating("checkers", playerName);
        List<Comment> comments = commentService.getComments("checkers");
        List<Score> scores = scoreService.getTopScores("checkers");

        System.out.printf("\n📊  ⭐ FINAL GAME STATS ⭐  📊\n");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("🎮 Game: %-8s | ⭐ %.2f/5\n", "Checkers", avgRating);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("%-1s | %-12s | %s | %-25s | %-20s | %-4s\n", "\u2116", "PLAYER", "SCORE", "COMMENT", "Date & Time", "RATING");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        //int maxEntries = Math.min(scores.size(), comments.size());
        for (int i = 0; i < scores.size(); i++) {
            var score = scores.get(i);
            var comment = (i < comments.size()) ? comments.get(i) : null;
            System.out.printf("%-2d | %-12s | %-5d | %-25s | %-20s | %-4d\n",
                    i + 1,
                    score.getPlayer(),
                    score.getPoints(),
                    (comment != null) ? comment.getComment() : "",
                    score.getPlayedOn(),
                    (rat != 0) ? rat : 0);
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }


    // Add comment
    public void addCom() {
        System.out.println("✍ Enter your comment: ");
        String commentText = input.nextLine().trim();

        if (commentText.isEmpty()) {
            System.out.println("⚠ Comment cannot be empty!");
            return;
        }

        String playerName = field.isWhiteTurn() ? nameWhitePlayer : nameBlackPlayer;
        commentService.addComment(new Comment("checkers", playerName, commentText, new Date()));

        System.out.println("✅ Comment added successfully!\n");

    }
    // Print comments
    public void printComs() {
        List<Comment> comments = commentService.getComments("checkers");

        System.out.println("\n💬  🎉  COMMENTS  🎉  💬");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("%-4s | %-15s | %-20s | %-20s\n", "No.", "Player", "Comment", "Date & Time");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (int i = 0; i < comments.size(); i++) {
            var comment = comments.get(i);
            System.out.printf("%-4d | %-15s | %-20s | %-20s\n",
                    i + 1,
                    comment.getPlayer(),
                    comment.getComment(),
                    String.format("%tF %tT", comment.getCommentedOn(), comment.getCommentedOn()));
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        input.nextLine();
    }

    // Save score
    public void saveScore() throws InterruptedException {
        scoreService.addScore(new Score("checkers", nameWhitePlayer, field.getScoreWhite(), new Date()));
        scoreService.addScore(new Score("checkers", nameBlackPlayer, field.getScoreBlack(), new Date()));
    }
    // Print scores
    public void printScores() {
        List<Score> scores = scoreService.getTopScores("checkers");

        System.out.println("\n🏆  🎉 LEADERBOARD 🎉  🏆");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("%-4s | %-12s | %s | %-20s\n", "🏅", "PLAYER", "SCORE", "Date & Time");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (int i = 0; i < scores.size(); i++) {
            var score = scores.get(i);
            System.out.printf("%-4d | %-12s | %-5d | %-20s\n",
                    i + 1,
                    score.getPlayer(),
                    score.getPoints(),
                    String.format("%tF %tT", score.getPlayedOn(), score.getPlayedOn()));
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        input.nextLine();
    }

    // Add rating
    public void addRat() {
        System.out.print("\n⭐ Enter your rating (1-5): ");
        int rating = input.nextInt();

        if (rating < 1 || rating > 5) {
            System.out.println("⚠ Rating must be between 1 and 5!\n");
            return;
        }

        String playerName = field.isWhiteTurn() ? nameWhitePlayer : nameBlackPlayer;
        ratingService.setRating(new Rating("checkers", playerName, rating, new Date()));

        System.out.println("✅ Rating added successfully!\n");
    }
    // Get and print average rating
    public void getAvgRating() {
        double avgRating = ratingService.getAverageRating("checkers");

        System.out.println("\n📊  ⭐ AVERAGE RATING ⭐  📊");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("🎮 Game: %-7s | ⭐ %.2f/5\n", "Checkers", avgRating);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }
}
