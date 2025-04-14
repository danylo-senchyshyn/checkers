package sk.tuke.gamestudio.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.rating.RatingService;
import sk.tuke.gamestudio.service.rating.RatingServiceJPA;
import sk.tuke.gamestudio.service.score.ScoreServiceJPA;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RatingServiceJPATest {
    @Autowired
    private RatingService ratingService;

    @BeforeEach
    public void setUp() {
        ratingService.reset();
    }

    @Test
    public void testSetAndGetRating() {
        ratingService.setRating(new Rating("checkers", "player1", 4, new Date()));
        int rating = ratingService.getRating("checkers", "player1");
        assertEquals(4, rating);
    }

    @Test
    public void testAverageRating() {
        ratingService.setRating(new Rating("checkers", "player1", 3, new Date()));
        ratingService.setRating(new Rating("checkers", "player2", 5, new Date()));
        double average = ratingService.getAverageRating("checkers");
        assertEquals(4, average);
    }

    @Test
    public void testUpdateRating() {
        ratingService.setRating(new Rating("checkers", "player1", 2, new Date()));
        ratingService.setRating(new Rating("checkers", "player1", 5, new Date())); // update
        int rating = ratingService.getRating("checkers", "player1");
        assertEquals(5, rating);
    }

    @Test
    public void testDeleteRatings() {
        ratingService.setRating(new Rating("checkers", "player1", 4, new Date()));
        ratingService.reset();
        int rating = ratingService.getRating("checkers", "player1");
        assertEquals(0.0, rating);
    }
}