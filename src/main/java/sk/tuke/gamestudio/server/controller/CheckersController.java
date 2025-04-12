package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.game.checkers.consoleui.ConsoleUI;
import sk.tuke.gamestudio.game.checkers.core.*;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

//http://localhost:8080
@Controller
@RequestMapping("/checkers")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class CheckersController {
    private CheckersField field = new CheckersField();
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private CommentService commentService;

    @GetMapping
    public String checkers(@RequestParam(required = false) Integer fr,
                           @RequestParam(required = false) Integer fc,
                           @RequestParam(required = false) Integer tr,
                           @RequestParam(required = false) Integer tc,
                           Model model) throws InterruptedException {
        if (fr != null && fc != null && tr != null && tc != null) {
            field.move(fr, fc, tr, tc);
            field.printField();
        }

        return "checkers";
    }

    @GetMapping("/new")
    public String newGame(Model model) {
        field = new CheckersField();
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
                                "<img src='/images/%s.png' class='background' alt='background'>",
                        row, col, row, col, colorBackground));

                if (!tile.isEmpty()) {
                    String pieceImage = getImageName(tile);
                    cell.append(String.format(
                            "<img src='/images/%s.png' class='piece-%s' id='piece-%d-%d' alt='%s'>",
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

        sb.append("<div id='player-turn' class='")
            .append(field.isWhiteTurn() ? "white-turn" : "black-turn")
            .append("'>")
            .append(String.format("%s's turn", field.isWhiteTurn() ? "White" : "Black"))
            .append("</div>\n");

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

    private void prepareModel(Model model) {
        model.addAttribute("scores", scoreService.getTopScores("checkers"));
        model.addAttribute("comments", commentService.getComments("checkers"));
        model.addAttribute("ratings", ratingService.getAverageRating("checkers"));
        model.addAttribute("field", field);
    }


}
