package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.game.checkers.core.*;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.score.ScoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/checkers")
@Scope(WebApplicationContext.SCOPE_SESSION)
@SessionAttributes({"player1", "player2", "avatar1", "avatar2"})
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
                           Model model) throws InterruptedException {
        String player1Name = (String) model.getAttribute("player1");
        String player2Name = (String) model.getAttribute("player2");
        String avatar1 = (String) model.getAttribute("avatar1");
        String avatar2 = (String) model.getAttribute("avatar2");

        model.addAttribute("player1", player1Name);
        model.addAttribute("player2", player2Name);
        model.addAttribute("avatar1", avatar1);
        model.addAttribute("avatar2", avatar2);

        if (fr != null && fc != null && tr != null && tc != null) {
            String currentPlayer = field.isWhiteTurn() ? "White" : "Black";
            boolean moveSuccess = field.move(fr, fc, tr, tc);
            if (moveSuccess) {
                recordMove(currentPlayer, fr, fc, tr, tc, field.isLastCaptured(), field.isLastBecameKing());
                field.printField();
            }
        }

        setPlayerInfo(model);
        List<String> reversedLog = new ArrayList<>(movesLog);
        Collections.reverse(reversedLog);
        model.addAttribute("movesLog", reversedLog);

        prepareModel(model);
        return "checkers";
    }

    @ModelAttribute
    public void setPlayerInfo(Model model) {
        String whitePlayerName = (String) model.getAttribute("player1");
        String whitePlayerAvatar = (String) model.getAttribute("avatar1");
        String blackPlayerName = (String) model.getAttribute("player2");
        String blackPlayerAvatar = (String) model.getAttribute("avatar2");

        model.addAttribute("whitePlayerName", whitePlayerName);
        model.addAttribute("whitePlayerAvatar", whitePlayerAvatar);
        model.addAttribute("blackPlayerName", blackPlayerName);
        model.addAttribute("blackPlayerAvatar", blackPlayerAvatar);

        int whitePlayerScore = field.getScoreWhite();
        int blackPlayerScore = field.getScoreBlack();

        model.addAttribute("whitePlayerScore", whitePlayerScore);
        model.addAttribute("blackPlayerScore", blackPlayerScore);
    }

    public void recordMove(String player, int fromRow, int fromCol, int toRow, int toCol, boolean captured, boolean becameKing) {
        String cssClass = "move-normal";

        if (captured && becameKing) {
            cssClass = "move-captured-kinged";
        } else if (captured) {
            cssClass = "move-captured";
        } else if (becameKing) {
            cssClass = "move-kinged";
        }

        movesLog.add(String.format("<li class=\"%s\">%s: (%d, %d) → (%d, %d)%s%s</li>",
                cssClass, player, fromRow, fromCol, toRow, toCol,
                captured ? " — взятие!" : "",
                becameKing ? " — стал дамкой!" : ""));
    }

    @GetMapping("/moves")
    @ResponseBody
    public List<int[]> getPossibleMoves(@RequestParam int row, @RequestParam int col) {
        return field.getPossibleMoves(row, col);
    }

    @GetMapping("/new")
    public String newGame(@RequestParam(required = false) String player1,
                          @RequestParam(required = false) String player2,
                          @RequestParam(required = false) String avatar1,
                          @RequestParam(required = false) String avatar2,
                          Model model) {
        model.addAttribute("player1", player1);
        model.addAttribute("player2", player2);
        model.addAttribute("avatar1", avatar1);
        model.addAttribute("avatar2", avatar2);


        field = new CheckersField();
        movesLog.clear();

        setPlayerInfo(model);
        model.addAttribute("movesLog", new ArrayList<>());

        prepareModel(model);
        return "checkers";
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

    public String getPlayerTurn() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='player-turn' class='")
                .append(field.isWhiteTurn() ? "white-turn" : "black-turn")
                .append("'>")
                .append(String.format("%s's turn", field.isWhiteTurn() ? "White" : "Black"))
                .append("</div>\n");
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

    private void prepareModel(Model model) {
        model.addAttribute("scores", scoreService.getTopScores("checkers"));
        model.addAttribute("comments", commentService.getComments("checkers"));
        double average = ratingService.getAverageRating("checkers");
        int rounded = (int) Math.round(average);
        model.addAttribute("averageRating", average);
        model.addAttribute("averageRatingRounded", rounded);
        model.addAttribute("htmlField", getHtmlField());

        model.addAttribute("field", field);
    }
}
