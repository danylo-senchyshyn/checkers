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
    private final ScoreService scoreService;
    private final RatingService ratingService;
    private final CommentService commentService;
    private final ConsoleColor consoleColor;
    private boolean isCommentWhite;
    private boolean isCommentBlack;
    private boolean isRatingWhite;
    private boolean isRatingBlack;
    private final String password = "admin";

    public ConsoleUI(Field field) {
        this.field = field;
        ScoreServiceJDBC scoreServiceJDBC = new ScoreServiceJDBC();
        CommentServiceJDBC commentServiceJDBC = new CommentServiceJDBC();
        RatingServiceJDBC ratingServiceJDBC = new RatingServiceJDBC();

        this.scoreService = scoreServiceJDBC;
        this.commentService = commentServiceJDBC;
        this.ratingService = ratingServiceJDBC;

        consoleColor = new ConsoleColor();
    }

    // Play the game
    public void play() throws InterruptedException {
        isCommentWhite = false;
        isCommentBlack = false;
        isRatingWhite = false;
        isRatingBlack = false;

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
            while (true) {
                printMenuAfterGame();
            }
        }
    }

    // Print welcome message
    private void printWelcomeMessage() {
        System.out.println("\n\n\n===================================");
        System.out.println("|   🎉 Welcome to Checkers! 🎉    |");
        System.out.println("===================================");

        input.nextLine();
    }

    private void inputNames() {
        System.out.println("Enter name of player 1: ");
        nameWhitePlayer = input.nextLine();
        System.out.println("Enter name of player 2: ");
        nameBlackPlayer = input.nextLine();

        System.out.println("\n🎮 Game started! Let's play!\n");
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
        System.out.println("\nGame state: " + gameStateMessage);

        System.out.printf("White score: %d\n", field.getScoreWhite());
        System.out.printf("Black score: %d\n", field.getScoreBlack());
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

    // Handle user input
    private void handleInput() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println(field.isWhiteTurn() ? "⚪ White's turn." : "⚫ Black's turn.");
        System.out.println(
                "Enter your move (e3 d4), or declare draw " +
                        consoleColor.pwc("'d'", ConsoleColor.YELLOW) +
                        " or exit game " +
                        consoleColor.pwc("'e'", ConsoleColor.RED) +
                        " : "
        );

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
        switch (field.getGameState()) {
            case WHITE_WON -> System.out.println("White wins! Congratulations!\n\n");
            case BLACK_WON -> System.out.println("Black wins! Congratulations!\n\n");
            case DRAW -> System.out.println("It's a draw! Well played!\n\n");
        }

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
        System.out.println("  🏆  'ss'  -  Show top scores");
        System.out.println("  ⭐  'sr'  -  Show average rating");
        System.out.println("  💬  'sc'  -  Show comments");
        System.out.println("  ✍️  'ac'  -  Add comment");
        System.out.println("  ⭐  'ar'  -  Add rating");
        System.out.println("  🔄  " + consoleColor.pwc("'rs'", ConsoleColor.BLUE) + "  -  Reset scores(admin)");
        System.out.println("  🔄  " + consoleColor.pwc("'rc'", ConsoleColor.BLUE) + "  -  Reset comments(admin)");
        System.out.println("  🔄  " + consoleColor.pwc("'rr'", ConsoleColor.BLUE) + "  -  Reset rating(admin)");
        System.out.println("  🎲  " + consoleColor.pwc("'sng'", ConsoleColor.GREEN) + " -  Start new game");
        System.out.println("  🚪  " + consoleColor.pwc("'ex'", ConsoleColor.RED) + "  -  Exit");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        System.out.print("🔹 Your input: ");
        String inputStr = input.nextLine().trim().toLowerCase();

        switch (inputStr) {
            case "ex" -> {
                System.out.println("\nThanks for playing!\n");
                System.exit(0);
            }
            case "ss" -> printScores();
            case "sr" -> getAvgRating();
            case "sc" -> printComs();
            case "rs" -> {
                if (askForPassword()){
                    scoreService.reset();
                } else {
                    input.close();
                    System.exit(1);
                }
            }
            case "rc" -> {
                if (askForPassword()){
                    commentService.reset();
                } else {
                    input.close();
                    System.exit(1);
                }
            }
            case "rr" -> {
                if (askForPassword()){
                    ratingService.reset();
                } else {
                    input.close();
                    System.exit(1);
                }
            }
            case "ac" -> {
                addCom();
            }
            case "ar" -> {
                collectRatings();
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

    // Password
    private boolean askForPassword() {
        System.out.println("\n🔑 Before using admin commands, please enter the password!\n");

        for (int i = 3; i > 0; i--) {
            System.out.print("\uD83D\uDD11 Password: ");
            if (input.nextLine().equals(password)) {
                System.out.println("\n✅Access granted!\n");
                input.nextLine();
                return true;
            }
            if (i > 1) {
                System.out.printf("\n❌ Invalid password! You have %d more tries.\n", i - 1);
            }
        }

        System.out.println("\nNo more tries!");
        System.out.println("\nThanks for playing!\n");
        return false;
    }

    // Comment
    private void addCom() {
        if (isCommentWhite && isCommentBlack) {
            System.out.println("✅ All players added comments\n");
            input.nextLine();
            return;
        }

        System.out.println("🎭 Who wants to add a comment? (w - White, b - Black): ");
        String choice = input.nextLine().trim().toLowerCase();

        if (choice.equals("w")) {
            addWhiteComment();
        } else if (choice.equals("b")) {
            addBlackComment();
        } else {
            System.out.println("⚠ Invalid choice! Please enter 'w' for White or 'b' for Black.\n");
            return;
        }
    }
    private void addWhiteComment() {
        if (isCommentWhite) {
            System.out.printf("⚠ %s has already added a comment.\n\n", nameWhitePlayer);
            return;
        }

        System.out.println("✍ Enter your comment (max 25 symbols): ");
        String commentText = input.nextLine().trim();

        if (commentText.isEmpty() || commentText.length() > 25) {
            System.out.println("⚠ Comment cannot be empty or longer than 25 symbols!\n");
            return;
        }

        isCommentWhite = true;
        commentService.addComment(new Comment("checkers", nameWhitePlayer, commentText, new Date()));
        System.out.printf("✅ Comment from %s added successfully!\n\n", nameWhitePlayer);
    }
    private void addBlackComment() {
        if (isCommentBlack) {
            System.out.printf("⚠ %s has already added a comment.\n\n", nameBlackPlayer);
            return;
        }

        System.out.println("✍ Enter your comment (max 25 symbols): ");
        String commentText = input.nextLine().trim();

        if (commentText.isEmpty() || commentText.length() > 25) {
            System.out.println("⚠ Comment cannot be empty or longer than 25 symbols!\n");
            return;
        }

        isCommentBlack = true;
        commentService.addComment(new Comment("checkers", nameBlackPlayer, commentText, new Date()));
        System.out.printf("✅ Comment from %s added successfully!\n\n", nameBlackPlayer);
        input.nextLine();
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
    private void collectRatings() {
        if (isRatingWhite && isRatingBlack) {
            System.out.println("✅ All players added rating\n");
            input.nextLine();
            return;
        }

        System.out.println("🎭 Who wants to add a rating? (w - White, b - Black): ");
        String choice = input.nextLine().trim().toLowerCase();

        if (choice.equals("w")) {
            collectRatingWhite();
        } else if (choice.equals("b")) {
            collectRatingBlack();
        } else {
            System.out.println("⚠ Invalid choice! Please enter 'w' for White or 'b' for Black.\n");
            return;
        }
    }
    private void collectRatingWhite() {
        if (isRatingWhite) {
            System.out.printf("⚠ %s has already added a rating.\n\n", nameWhitePlayer);
            return;
        }

        boolean validRating = false;
        int parsedRating = 0;

        while (!validRating) {
            System.out.printf("🌟 %s, please enter your rating (1-5): ", nameWhitePlayer);
            String rating = input.nextLine().trim();

            if (rating.matches("^[1-5]$")) {
                parsedRating = Integer.parseInt(rating);
                validRating = true;
            } else {
                System.out.println("⚠ Invalid input! Please enter a number between 1 and 5.\n");
            }
        }

        isRatingWhite = true;
        ratingService.setRating(new Rating("checkers", nameWhitePlayer, parsedRating, new Date()));
        System.out.printf("🎉 Thank you, %s! Your rating of %d ⭐ has been recorded. 🙌\n\n", nameWhitePlayer, parsedRating);
    }
    private void collectRatingBlack() {
        if (isRatingBlack) {
            System.out.printf("⚠ %s has already added a rating.\n\n", nameBlackPlayer);
            return;
        }

        boolean validRating = false;
        int parsedRating = 0;

        while (!validRating) {
            System.out.printf("🌟 %s, please enter your rating (1-5): ", nameBlackPlayer);
            String rating = input.nextLine().trim();

            if (rating.matches("^[1-5]$")) {
                parsedRating = Integer.parseInt(rating);
                validRating = true;
            } else {
                System.out.println("⚠ Invalid input! Please enter a number between 1 and 5.\n");
            }
        }

        isRatingBlack = true;
        ratingService.setRating(new Rating("checkers", nameBlackPlayer, parsedRating, new Date()));
        System.out.printf("🎉 Thank you, %s! Your rating of %d ⭐ has been recorded. 🙌\n\n", nameBlackPlayer, parsedRating);
    }
    private void getAvgRating() {
        double avgRating = ratingService.getAverageRating("checkers");

        System.out.println("\n📊  ⭐ AVERAGE RATING ⭐  📊");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("🎮 Game: %-7s | ⭐ %.1f/5.0\n", "Checkers", avgRating);
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