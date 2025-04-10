package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.game.checkers.core.CheckersField;
import sk.tuke.gamestudio.game.checkers.core.Tile;
import sk.tuke.gamestudio.service.*;

//http://localhost:8080
@RestController
@RequestMapping("/checkers")
public class CheckersController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private UserController userController;
    @Autowired
    private CheckersField field;

    @GetMapping("/field")
    @ResponseBody
    public Tile[][] getField() {
        return field.getField();
    }

    @PostMapping("/move")
    @ResponseBody
    public boolean move(@RequestBody MoveRequest move) {
        return field.move(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='checkers-field' id='board'>\n");
        for (int row = 0; row < 8; row++) {
            sb.append("<tr>\n");
            for (int column = 0; column < 8; column++) {
                Tile tile = field.getField()[row][column];
                String imageName = getImageName(tile);
                String className = (row + column) % 2 == 0 ? "white-square" : "black-square";
                String tileId = "tile-" + row + "-" + column;

                sb.append("<td class='" + className + "' id='" + tileId + "'>\n");

                if (!imageName.isEmpty()) {
                    sb.append("<img src='/images/" + imageName + ".png' alt='" + imageName + "' "
                            + "id='piece-" + row + "-" + column + "' "
                            + "draggable='true' "
                            + "ondragstart='dragStart(event)' />\n");
                }

                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        return sb.toString();
    }

    private void prepareModel(Model model) throws CommentException, RatingException, ScoreException {
        model.addAttribute("scores", scoreService.getTopScores("checkers"));
        model.addAttribute("comments", commentService.getComments("checkers"));
        model.addAttribute("averageRating", ratingService.getAverageRating("checkers"));
        model.addAttribute("field", field);
    }

    public String getTurn() {
        return "<div id='turn-indicator'>" +
                (field.isWhiteTurn() ? "White's turn" : "Black's turn") +
                "</div>";
    }

    public String getGameState() {
        switch (field.getGameState()) {
            case PLAYING:
                return field.isWhiteTurn() ? "White's turn" : "Black's turn";
            case WHITE_WON:
                return "White won!";
            case BLACK_WON:
                return "Black won!";
            case DRAW:
                return "Draw!";
            default:
                return "Unknown state";
        }
    }

    private String getImageName(Tile tile) {
        if (tile == null) {
            return "";
        }

        switch (tile.getState()) {
            case WHITE:
                return "white_checker";
            case BLACK:
                return "black_checker";
            case WHITE_KING:
                return "white_king";
            case BLACK_KING:
                return "black_king";
            default:
                return "";
        }
    }

    @RequestMapping("/new")
    public String newGame(Model model) {
        field.startNewGame();
        prepareModel(model);
        return "checkers";
    }
}
