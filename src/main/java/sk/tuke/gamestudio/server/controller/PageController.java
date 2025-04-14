package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.score.ScoreService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PageController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    @GetMapping("/")
    public String home() {
        return "home_page";
    }

    @GetMapping("/rules")
    public String rules() {
        return "rules";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/reviews")
    public String showReviews(Model model) {
        List<Comment> comments = commentService.getComments("checkers");
        double averageRating = ratingService.getAverageRating("checkers");
        int reviewCount = comments.size();

        Map<String, Integer> ratings = comments.stream()
                .collect(Collectors.toMap(
                        Comment::getPlayer,
                        c -> {
                            int rating = ratingService.getRating("checkers", c.getPlayer());
                            return rating == 0 ? null : rating;
                        },
                        (r1, r2) -> r1
                ));

        model.addAttribute("comments", comments);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("ratings", ratings); // <-- исправлено здесь

        return "reviews";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        List<Score> scores = scoreService.getTopScores("checkers");
        System.out.println("Top scores: " + scores.size());
        model.addAttribute("scores", scores);
        return "leaderboard";
    }
}