package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.*;
import sk.tuke.gamestudio.game.checkers.core.CheckersField;
import sk.tuke.gamestudio.game.checkers.core.GameState;
import sk.tuke.gamestudio.game.checkers.core.Tile;
import sk.tuke.gamestudio.game.checkers.core.TileState;
import sk.tuke.gamestudio.service.*;

import javax.sound.sampled.Line;
import java.util.Date;

//http://localhost:8080/checkers
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
    private GameState gameState;

    @GetMapping("/checkers")
    public String move(@RequestParam int fromRow, @RequestParam int fromCol,
                       @RequestParam int toRow, @RequestParam int toCol, Model model) {
        System.out.println("-------------------------------------------------------------------------------");
        String errorMessage = null;

        try {
            if (field.getGameState() == GameState.PLAYING) {
                boolean moveSuccess = field.move(fromRow, fromCol, toRow, toCol);

                if (!moveSuccess) {
                    errorMessage = "Invalid move!";
                }

                if (moveSuccess && field.getGameState() != GameState.PLAYING) {
                    scoreService.addScore(new Score("checkers", "danylo", field.getScoreWhite(), new Date()));
                }
                System.out.println("Move from " + fromRow + "," + fromCol + " to " + toRow + "," + toCol);
                System.out.println("Success: " + moveSuccess);
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid coordinates!";
        }

        model.addAttribute("errorMessage", errorMessage);
        prepareModel(model);
        model.addAttribute("field", getHtmlField());

        return "checkers";
    }

    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='board-container'>\n");

        sb.append("<table class='field'>\n");
        for (int row = 0; row < 8; row++) {
            sb.append("<tr>\n");
            for (int col = 0; col < 8; col++) {
                Tile tile = field.getField()[row][col];
                String tileClass = (row + col) % 2 == 0 ? "light" : "dark";

                sb.append("<td class='").append(tileClass)
                        .append("' data-row='").append(row)
                        .append("' data-col='").append(col).append("'>");

                sb.append("<div class='tile-inner' data-row='" + row + "' data-col='" + col + "' ");

                String imageName = getImageName(tile);
                if (!imageName.isEmpty()) {
                    sb.append(">");
                    sb.append("<img src='/images/").append(imageName)
                            .append(".png' draggable='true'>\n");
                } else {
                    sb.append(">");
                }

                sb.append("</div></td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        sb.append("</div>\n");

        return sb.toString();
    }

    private void prepareModel(Model model) throws CommentException, RatingException {
        model.addAttribute("scores", scoreService.getTopScores("checkers"));
        model.addAttribute("comments", commentService.getComments("checkers"));
        model.addAttribute("averageRating", ratingService.getAverageRating("checkers"));
        if (userController.isLogged()) {
            model.addAttribute("Rating.getRating", ratingService.getRating("checkers", userController.getLoggedUser()));
        }
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
    public String newGame(Model model) throws CommentException, RatingException {
        field.startNewGame();
        prepareModel(model);

        return "checkers";
    }

    @RequestMapping("/addRat")
    public String addRating(String player, String rating, Model model) throws RatingException {
        ratingService.setRating(new Rating("checkers", player, Integer.parseInt(rating), new Date()));
        prepareModel(model);

        return "checkers";
    }

    @RequestMapping("/addCom")
    public String addComment(String player, String comment, Model model) throws CommentException {
        commentService.addComment(new Comment("checkers", player, comment, new Date()));
        prepareModel(model);

        return "checkers";
    }
}
