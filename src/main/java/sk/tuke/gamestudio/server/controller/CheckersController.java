package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.checkers.core.*;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.score.ScoreService;

import java.util.*;

@Controller
@RequestMapping("/checkers")
@Scope(WebApplicationContext.SCOPE_SESSION)
@SessionAttributes({"player1", "player2", "avatar1", "avatar2", "isWhiteTurn"})
public class CheckersController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private CommentService commentService;

    private CheckersField field = new CheckersField();
    private final List<String> movesLog = new ArrayList<>();

    @GetMapping
    public String checkers(@RequestParam(required = false) Integer fr,
                           @RequestParam(required = false) Integer fc,
                           @RequestParam(required = false) Integer tr,
                           @RequestParam(required = false) Integer tc,
                           @SessionAttribute(name = "player1", required = false) String player1,
                           @SessionAttribute(name = "player2", required = false) String player2,
                           Model model) throws InterruptedException {
        if (fr != null && fc != null && tr != null && tc != null) {
            boolean moveSuccess = field.move(fr, fc, tr, tc);
            if (moveSuccess) {
                recordMove(fr, fc, tr, tc);
            }
        }

        List<String> reversedLog = new ArrayList<>(movesLog);
        Collections.reverse(reversedLog);
        model.addAttribute("movesLog", reversedLog);

        prepareModel(player1, player2, model);
        return "checkers";
    }

    private void prepareModel(@SessionAttribute(name = "player1", required = false) String player1,
                              @SessionAttribute(name = "player2", required = false) String player2,
                              Model model) {
        model.addAttribute("scores", scoreService.getTopScores("checkers"));
        model.addAttribute("comments", commentService.getComments("checkers"));
        double average = ratingService.getAverageRating("checkers");
        int rounded = (int) Math.round(average);
        model.addAttribute("averageRating", average);
        model.addAttribute("averageRatingRounded", rounded);
        model.addAttribute("whitePlayerScore", field.getScoreWhite());
        model.addAttribute("blackPlayerScore", field.getScoreBlack());
        model.addAttribute("isWhiteTurn", field.isWhiteTurn());

        model.addAttribute("htmlField", getHtmlField());
        model.addAttribute("field", field);

        if (field.getGameState() != GameState.PLAYING) {
            model.addAttribute("gameOver", true);
            String winner = field.getGameState() == GameState.WHITE_WON ? player1 : player2;
            int score = field.getGameState() == GameState.WHITE_WON ? field.getScoreWhite() : field.getScoreBlack();

            model.addAttribute("winner", winner);
            System.out.println(winner);

            boolean scoreAlreadyExists = scoreService.getTopScores("checkers").stream()
                    .anyMatch(existingScore -> existingScore.getPlayer().equals(winner));

            if (!scoreAlreadyExists) {
                scoreService.addScore(new Score("checkers", winner, score, new Date()));
            }
        }
    }

    @PostMapping("/save-players")
    public ResponseEntity<Void> savePlayers(@RequestBody Map<String, String> data, Model model) {
        model.addAttribute("player1", data.get("player1"));
        model.addAttribute("player2", data.get("player2"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/new")
    public String newGame(@SessionAttribute(name = "player1", required = false) String player1,
                          @SessionAttribute(name = "player2", required = false) String player2,
                          Model model) {
        field.startNewGame();
        movesLog.clear();

        model.addAttribute("movesLog", new ArrayList<>());

        prepareModel(player1, player2, model);
        return "checkers";
    }

    public void recordMove(int fromRow, int fromCol, int toRow, int toCol) {
        String cssClass = "move-normal";
        boolean captured = field.isLastCaptured();
        boolean becameKing = field.isLastBecameKing();

        if (captured && becameKing) {
            cssClass = "move-captured-kinged";
        } else if (captured) {
            cssClass = "move-captured";
        } else if (becameKing) {
            cssClass = "move-kinged";
        }

        movesLog.add(String.format("<li class=\"%s\">%s: (%d, %d) → (%d, %d)</li>",
                cssClass, field.isWhiteTurn() ? "White" : "Black", fromRow, fromCol, toRow, toCol)
        );
    }

    @GetMapping("/moves")
    @ResponseBody
    public List<int[]> getPossibleMoves(@RequestParam int row, @RequestParam int col) {
        return field.getPossibleMoves(row, col);
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();

        sb.append("<table class='checkers-field'>\n");
        for (int row = 0; row < 8; row++) {
            sb.append("<tr>\n");
            for (int col = 0; col < 8; col++) {
                Tile tile = field.getField()[row][col];
                String colorBackground = (row + col) % 2 == 0 ? "empty_white" : "empty_black";
                StringBuilder cell = new StringBuilder();

                cell.append(String.format(
                        "<td id='tile-%d-%d' class='tile' onclick='selectTile(%d, %d)'>" +
                                "<div class='background'>" +
                                "<img src='/images/board/%s.png' class='background' alt='background'>",
                        row, col, row, col, colorBackground));

                if (!tile.isEmpty()) {
                    String pieceImage = getImageName(tile);
                    cell.append(String.format(
                            "<img src='/images/board/%s.png' class='piece-%s' id='piece-%d-%d' alt='%s'>",
                            pieceImage,
                            tile.isChecker() ? "checker" : "king",
                            row, col, pieceImage));
                }

                cell.append("</div></td>\n");

                sb.append(cell);
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");

        return sb.toString();
    }

    public String getScorePlayers() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='score-board'>")
                .append(String.format("<div class='score'>White: %d</div>\n", field.getScoreWhite()))
                .append(String.format("<div class='score'>Black: %d</div>\n", field.getScoreBlack()));
        return sb.toString();
    }

    private String getImageName(Tile tile) {
        switch (tile.getState()) {
            case WHITE:
                return "white_checker";
            case BLACK:
                return "black_checker";
            case WHITE_KING:
                return "white_king";
            case BLACK_KING:
                return "black_king";
            case EMPTY_WHITE:
                return "empty_white";
            case EMPTY_BLACK:
                return "empty_black";
            default:
                return "";
        }
    }
}
