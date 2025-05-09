package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.service.comment.CommentService;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.score.ScoreService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionAttributes({"player1", "player2", "avatar1", "avatar2"})
public class PageController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activePage", "home");
        return "home_page";
    }

    @GetMapping("/rules")
    public String rules(Model model) {
        model.addAttribute("activePage", "rules");
        return "rules";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activePage", "about");
        return "about";
    }

    @GetMapping("/reviews")
    public String getReviews(@RequestParam(defaultValue = "1") int page,
                             @SessionAttribute(name = "player1", required = false) String player1,
                             @SessionAttribute(name = "player2", required = false) String player2,
                             @SessionAttribute(name = "avatar1", required = false) String avatar1,
                             @SessionAttribute(name = "avatar2", required = false) String avatar2,
                             Model model) {
        model.addAttribute("activePage", "reviews");

        int pageSize = 5;
        List<Comment> allComments = commentService.getComments("checkers");
        int totalComments = allComments.size();
        int totalPages = (int) Math.ceil((double) totalComments / pageSize);

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalComments);
        List<Comment> commentsPage = allComments.subList(start, end);

        Map<String, Integer> playerRatings = new HashMap<>();
        for (Comment comment : commentsPage) {
            int rating = ratingService.getRating("checkers", comment.getPlayer());
            playerRatings.put(comment.getPlayer(), rating);
        }

        int range = 5;
        int startPage = Math.max(1, page - range / 2);
        int endPage = Math.min(totalPages, startPage + range - 1);

        double average = ratingService.getAverageRating("checkers");
        int rounded = (int) Math.round(average);

        model.addAttribute("averageRating", average);
        model.addAttribute("averageRatingRounded", rounded);
        model.addAttribute("comments", commentsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("ratings", playerRatings);
        model.addAttribute("reviewCount", totalComments);

        model.addAttribute("player1", player1);
        model.addAttribute("player2", player2);
        model.addAttribute("avatar1", avatar1);
        model.addAttribute("avatar2", avatar2);

        return "reviews";
    }

    @PostMapping("/add-review")
    public String addReview(
            @RequestParam String player,
            @RequestParam String comment,
            @RequestParam int rating
    ) {
        commentService.addComment(new Comment("checkers", player, comment, new Date()));
        ratingService.setRating(new Rating("checkers", player, rating, new Date()));

        return "redirect:/reviews";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(@SessionAttribute(name = "player1", required = false) String player1,
                              @SessionAttribute(name = "player2", required = false) String player2,
                              @SessionAttribute(name = "avatar1", required = false) String avatar1,
                              @SessionAttribute(name = "avatar2", required = false) String avatar2,
                              Model model) {
        model.addAttribute("activePage", "leaderboard");
        List<Score> scores = scoreService.getTopScores("checkers");
        model.addAttribute("scores", scores);

        model.addAttribute("player1", player1);
        model.addAttribute("player2", player2);
        model.addAttribute("avatar1", avatar1);
        model.addAttribute("avatar2", avatar2);

        return "leaderboard";
    }
}