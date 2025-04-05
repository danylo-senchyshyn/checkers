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
import sk.tuke.gamestudio.service.*;

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

    public boolean isPlaying() {
        return field.getGameState() == GameState.PLAYING;
    }

    @RequestMapping("/new")
    public String newGame(Model model) throws CommentException, RatingException {
        field.startNewGame();
        prepareModel(model);
        return "checkers";
    }

    private void prepareModel(Model model) throws CommentException, RatingException {
        model.addAttribute("scores", scoreService.getTopScores("checkers"));
        model.addAttribute("comments", commentService.getComments("checkers"));
        model.addAttribute("averageRating", ratingService.getAverageRating("checkers"));
        if (userController.isLogged()) {
            model.addAttribute("Rating.getRating", ratingService.getRating("checkers", userController.getLoggedUser()));
        }
    }

    @RequestMapping("/addRat")
    public String addRating(String player, String rating, Model model) throws RatingException, CommentException {
        ratingService.setRating(new Rating("checkers", player, Integer.parseInt(rating), new Date()));
        prepareModel(model);

        return "checkers";
    }

    @RequestMapping("/addCom")
    public String addComment(String player, String comment, Model model) throws CommentException, RatingException {
        commentService.addComment(new Comment("checkers", player, comment, new Date()));
        prepareModel(model);

        return "checkers";
    }

}
