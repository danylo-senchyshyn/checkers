//package sk.tuke.gamestudio.service;
//
//import org.junit.Before;
//import org.junit.Test;
//import sk.tuke.kpi.kp.entity.Rating;
//import sk.tuke.kpi.kp.service.RatingService;
//import sk.tuke.kpi.kp.service.RatingServiceJDBC;
//
//import java.util.Date;
//
//import static org.junit.Assert.assertEquals;
//
//
//public class RatingServiceTest {
//    RatingService ratingService;
//
//    @Before
//    public void setUp() {
//        ratingService = new RatingServiceJDBC();
//    }
//
//    @Test
//    public void resetTest() {
//        ratingService.reset();
//
//        Date date = new Date();
//        ratingService.setRating(new Rating("checkers", "player1", 4, date));
//        ratingService.setRating(new Rating("checkers", "player2", 5, date));
//
//        assertEquals(4, ratingService.getRating("checkers", "player1"));
//        assertEquals(5, ratingService.getRating("checkers", "player2"));
//
//        ratingService.reset();
//
//        assertEquals(0, ratingService.getRating("checkers", "player1"));
//        assertEquals(0, ratingService.getRating("checkers", "player2"));
//
//        ratingService.reset();
//    }
//
//    @Test
//    public void setRatingTest() {
//        ratingService.reset();
//
//        Date date = new Date();
//        ratingService.setRating(new Rating("checkers", "player1", 5, date));
//        int rating = ratingService.getRating("checkers", "player1");
//
//        assertEquals(5, rating);
//
//        ratingService.reset();
//    }
//
//    @Test
//    public void getAverageRatingTest() {
//        ratingService.reset();
//
//        Date date = new Date();
//
//        ratingService.setRating(new Rating("checkers", "player1", 4, date));
//        ratingService.setRating(new Rating("checkers", "player2", 3, date));
//        ratingService.setRating(new Rating("checkers", "player3", 5, date));
//
//        double averageRating = ratingService.getAverageRating("checkers");
//        assertEquals(4.00, averageRating, 0.01);
//
//        ratingService.setRating(new Rating("checkers", "player4", 2, date));
//        averageRating = ratingService.getAverageRating("checkers");
//        assertEquals(3.50, averageRating, 0.01);
//
//        ratingService.reset();
//    }
//}
