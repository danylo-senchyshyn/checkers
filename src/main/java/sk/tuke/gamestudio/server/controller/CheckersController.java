package sk.tuke.gamestudio.server.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.game.checkers.core.CheckersField;
import sk.tuke.gamestudio.game.checkers.core.Tile;
import sk.tuke.gamestudio.game.checkers.core.TileState;
import sk.tuke.gamestudio.service.*;

import java.util.HashMap;
import java.util.Map;

//http://localhost:8080
@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
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

    @GetMapping("/checkers")
    public String checkers(@RequestParam(required = false) Integer fr,
                       @RequestParam(required = false) Integer fc,
                       @RequestParam(required = false) Integer tr,
                       @RequestParam(required = false) Integer tc,
                       Model model) {
        if (fr != null && fc != null && tr != null && tc != null) {
            TileState tileState = field.getField()[fr][fc].getState();

            boolean isWhiteFigure = tileState == TileState.WHITE || tileState == TileState.WHITE_KING;
            boolean isBlackFigure = tileState == TileState.BLACK || tileState == TileState.BLACK_KING;

            boolean correctTurn = (field.isWhiteTurn() && isWhiteFigure) ||
                    (!field.isWhiteTurn() && isBlackFigure);

            if (correctTurn) {
                boolean moved = field.move(fr, fc, tr, tc);
                if (!moved) {
                    model.addAttribute("error", "Недопустимый ход!");
                }
            } else {
                model.addAttribute("error", "Теперь ход " + (field.isWhiteTurn() ? "белых" : "чёрных") + "!");
            }
        }

        prepareModel(model);
        return "checkers";
    }

    @PostMapping("/move")
    public ResponseEntity<Map<String, Object>> move(@RequestBody MoveRequest moveRequest) {
        Map<String, Object> response = new HashMap<>();
        boolean moved = field.move(moveRequest.getFromRow(), moveRequest.getFromCol(), moveRequest.getToRow(), moveRequest.getToCol());

        if (moved) {
            response.put("success", true);
            response.put("fieldHtml", getHtmlField()); // Отправляем обновленное поле в HTML
        } else {
            response.put("success", false);
            response.put("message", "Недопустимый ход!");
        }
        return ResponseEntity.ok(response);
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='checkers-field' id='checkers-field'>\n");
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

        sb.append("<div id='turn-indicator'>")
                .append(field.isWhiteTurn() ? "White's turn" : "Black's turn")
                .append("</div>");

        return sb.toString();
    }

    private void prepareModel(Model model) throws CommentException, RatingException, ScoreException {
        model.addAttribute("scores", scoreService.getTopScores("checkers"));
        model.addAttribute("comments", commentService.getComments("checkers"));
        model.addAttribute("averageRating", ratingService.getAverageRating("checkers"));
        model.addAttribute("field", field);
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
