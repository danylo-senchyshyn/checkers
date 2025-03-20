package sk.tuke.kpi.kp.game.checkers.consoleui;

import sk.tuke.kpi.kp.game.checkers.core.Field;
import sk.tuke.kpi.kp.game.checkers.core.GameState;
import sk.tuke.kpi.kp.entity.Comment;
import sk.tuke.kpi.kp.entity.Rating;
import sk.tuke.kpi.kp.entity.Score;
import sk.tuke.kpi.kp.service.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner input = new Scanner(System.in);
    private final Field field;
    private String nameWhitePlayer = null;
    private String nameBlackPlayer = null;
    private ScoreService scoreService;
    private RatingService ratingService;
    private CommentService commentService;
    private final ScoreServiceJDBC scoreServiceJDBC;
    private final CommentServiceJDBC commentServiceJDBC;
    private final RatingServiceJDBC ratingServiceJDBC;
    private boolean isExit;
    private boolean isComment;
    private boolean isRating;

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
        isExit = false;
        isComment = false;
        isRating = false;

        if (nameBlackPlayer == null || nameWhitePlayer == null) {
            printWelcomeMessage();
            inputNames();
        }

        while (field.getGameState() == GameState.PLAYING) {
            displayGameStats();
            printBoard();
            handleInput();
        }

        if (field.getGameState() != GameState.PLAYING) {
            saveScore();
            gameOver();
            while(!isExit) {
                printMenuAfterGame();
            }
        }
    }

    // Print welcome message
    private void printWelcomeMessage() {
        System.out.println("\n\n\n==================================================");
        System.out.println("|        🎉 Welcome to Checkers! 🎉              |");
        System.out.println("|------------------------------------------------|");
        System.out.println("| * Regular pieces move diagonally forward.      |");
        System.out.println("| * Kings move diagonally in any direction.      |");
        System.out.println("| * Plan your strategy and outsmart your rival!  |");
        System.out.println("|------------------------------------------------|");
        System.out.println("|      ✨ Good luck and have fun! ✨             |");
        System.out.println("==================================================");

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

    // Handle user input
    private void handleInput() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println(field.isWhiteTurn() ? "⚪ White's turn." : "⚫ Black's turn.");
        System.out.println("Enter your move (e3 d4), or declare draw 'd' or exit game 'e': ");

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

        System.out.println("\nThanks for playing!\n");
    }

    private void startNewGame() throws InterruptedException {
        field.createNewGame();
        play();
    }

    private void printMenuAfterGame() {
        System.out.println("You can: ");
        System.out.println("  🏆  'ss' - Show top scores");
        System.out.println("  ⭐  'sr' - Show average rating");
        System.out.println("  💬  'sc' - Show comments");
        System.out.println("  🔄  'rs' - Reset scores");
        System.out.println("  🔄  'rc' - Reset comments");
        System.out.println("  ✍️  'ac' - Add comment");
        System.out.println("  ⭐  'ar' - Add rating");
        System.out.println("  🎲  'sng' - Start new game");
        System.out.println("  🚪  'e'  - Exit");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        System.out.print("🔹 Your input: ");
        String inputStr = input.nextLine().trim().toLowerCase();

        switch (inputStr) {
            case "e" -> {
                isExit = true;
            }
            case "ss" -> printScores();
            case "sr" -> getAvgRating();
            case "sc" -> printComs();
            case "rs" -> scoreService.reset();
            case "rc" -> commentService.reset();
            case "ac" -> {
                addCom();
                isComment = true;
            }
            case "ar" -> {
                collectRatings(nameWhitePlayer, nameBlackPlayer);
                isRating = true;
            }
            case "sng" -> {
                try {
                    startNewGame();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> {
                System.out.println("❌ Invalid option, try again!");
                printMenuAfterGame();
            }
        }
    }

    // Comment
    private void addCom() {
        if (isComment) {
            System.out.println("You already add comment");
            return;
        }

        System.out.println("🎭 Who wants to add a comment? (w - White, b - Black): ");
        String playerChoice = input.nextLine().trim().toLowerCase();

        String playerName;
        if ("w".equals(playerChoice)) {
            playerName = nameWhitePlayer;
        } else if ("b".equals(playerChoice)) {
            playerName = nameBlackPlayer;
        } else {
            System.out.println("⚠ Invalid choice! Please enter 'w' for White or 'b' for Black.");
            addCom();
            return;
        }
        System.out.println("✍ Enter your comment: ");
        String commentText = input.nextLine().trim();

        if (commentText.isEmpty() || commentText.length() > 25) {
            System.out.println("⚠ Comment cannot be empty or longer then 25 symbols!");
            return;
        }

        commentService.addComment(new Comment("checkers", playerName, commentText, new Date()));

        System.out.println("✅ Comment added successfully!\n");
    }
    private void printComs() {
        List<Comment> comments = commentService.getComments("checkers");

        System.out.println("\n💬  🎉  COMMENTS  🎉  💬");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("%-2s | %-15s | %-20s | %-20s\n", "\u2116", "Player", "Comment", "Date & Time");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (int i = 0; i < comments.size(); i++) {
            var comment = comments.get(i);
            System.out.printf("%-2d | %-15s | %-20s | %-20s\n",
                    i + 1,
                    comment.getPlayer(),
                    comment.getComment(),
                    String.format("%tF %tT", comment.getCommentedOn(), comment.getCommentedOn()));
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        input.nextLine();
    }

    // Rating
    private void collectRatings(String nameWhitePlayer, String nameBlackPlayer) {
        if (isRating) {
            System.out.println("You already added rating!");
            return;
        }

        collectRatingForPlayer(nameWhitePlayer);
        collectRatingForPlayer(nameBlackPlayer);
    }
    private void collectRatingForPlayer(String playerName) {
        System.out.printf("🌟 %s, please enter your rating (1-5): ", playerName);
        String rating = input.nextLine().trim();

        if (!rating.matches("^[1-5]$")) {
            System.out.println("⚠ Invalid input! Please enter a number between 1 and 5.\n");
            collectRatingForPlayer(playerName);
            return;
        }

        int parsedRating = Integer.parseInt(rating);
        ratingService.setRating(new Rating("checkers", playerName, parsedRating, new Date()));

        System.out.printf("🎉 Thank you, %s! Your rating of %d ⭐ has been recorded. 🙌\n\n", playerName, parsedRating);
    }
    private void getAvgRating() {
        double avgRating = ratingService.getAverageRating("checkers");

        System.out.println("\n📊  ⭐ AVERAGE RATING ⭐  📊");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("🎮 Game: %-7s | ⭐ %.2f/5\n", "Checkers", avgRating);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        input.nextLine();
    }

    // Score
    private void saveScore() {
        scoreService.addScore(new Score("checkers", nameWhitePlayer, field.getScoreWhite(), new Date()));
        scoreService.addScore(new Score("checkers", nameBlackPlayer, field.getScoreBlack(), new Date()));
    }
    private void printScores() {
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
}