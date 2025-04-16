package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String getReviews(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 5;
        List<Comment> allComments = commentService.getComments("checkers");
        int totalComments = allComments.size();
        int totalPages = (int) Math.ceil((double) totalComments / pageSize);

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalComments);
        List<Comment> commentsPage = allComments.subList(start, end);

        Map<String, Double> playerRatings = new HashMap<>();
        for (Comment comment : commentsPage) {
            double rating = ratingService.getRating("checkers", comment.getPlayer());
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

        return "reviews";
    }

    @PostMapping("/add-review")
    public String addReview(
            @RequestParam String player,
            @RequestParam String comment,
            @RequestParam int rating
    ) {
        Comment newComment = new Comment();
        newComment.setGame("checkers");
        newComment.setPlayer(player);
        newComment.setComment(comment);
        newComment.setCommentedOn(new Date());
        commentService.addComment(newComment);

        ratingService.setRating(new Rating("checkers", player, rating, new Date()));

        return "redirect:/reviews";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        List<Score> scores = scoreService.getTopScores("checkers");
        System.out.println("Top scores: " + scores.size());
        model.addAttribute("scores", scores);
        return "leaderboard";
    }
}