package sk.tuke.gamestudio.service;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.entity.Rating;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatingServiceTest {
    RatingService ratingService;

    @BeforeEach
    public void setUp() {
        ratingService = new RatingServiceJDBC();
    }

    @Test
    public void resetTest() {
        ratingService.reset();

        Date date = new Date();
        ratingService.setRating(new Rating("checkers", "player1", 4, date));
        ratingService.setRating(new Rating("checkers", "player2", 5, date));

        assertEquals(4, ratingService.getRating("checkers", "player1"));
        assertEquals(5, ratingService.getRating("checkers", "player2"));

        ratingService.reset();

        assertEquals(0, ratingService.getRating("checkers", "player1"));
        assertEquals(0, ratingService.getRating("checkers", "player2"));

        ratingService.reset();
    }

    @Test
    public void setRatingTest() {
        ratingService.reset();

        Date date = new Date();
        ratingService.setRating(new Rating("checkers", "test-player1", 5, date));
        int rating = ratingService.getRating("checkers", "test-player1");

        assertEquals(5, rating);

        ratingService.reset();
    }

    @Test
    public void getAverageRatingTest() {
        ratingService.reset();

        Date date = new Date();

        ratingService.setRating(new Rating("checkers", "test-player1", 4, date));
        ratingService.setRating(new Rating("checkers", "test-player2", 3, date));

        double averageRating = ratingService.getAverageRating("checkers");
        assertEquals(3.5, averageRating, 0.1);

        ratingService.setRating(new Rating("checkers", "test-player4", 1, date));
        averageRating = ratingService.getAverageRating("checkers");
        assertEquals(2.7, averageRating, 0.1);

        ratingService.reset();
    }
}
