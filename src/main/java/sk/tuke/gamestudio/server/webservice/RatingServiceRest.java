package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.*;

@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {
    @Autowired
    private RatingService ratingService;

    @PostMapping
    public void setRating(@RequestBody Rating rating) throws RatingException {
        ratingService.setRating(rating);
    }

    @GetMapping("/{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) throws RatingException {
        return ratingService.getRating(game, player);
    }

    @GetMapping("/{game}")
    public double getAverageRating(@PathVariable String game) throws RatingException {
        return ratingService.getAverageRating(game);
    }

    @DeleteMapping
    public void reset() throws RatingException {
        ratingService.reset();
    }
}
