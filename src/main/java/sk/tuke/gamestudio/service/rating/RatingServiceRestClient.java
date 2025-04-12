package sk.tuke.gamestudio.service.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

@Service
public class RatingServiceRestClient implements RatingService {
    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void setRating(Rating rating) throws RatingException {
        try {
            restTemplate.postForEntity(url, rating, Rating.class);
        } catch (HttpServerErrorException e) {
            throw new RatingException("Error setting rating", e);
        }
    }

    @Override
    public int getRating(String game, String username) throws RatingException {
        int rating = 0;
        try {
            Rating ratingObject = restTemplate.getForEntity(url + "/" + game + "/" + username, Rating.class).getBody();
            if (ratingObject != null) {
                rating = ratingObject.getRating();
            }
        } catch (HttpServerErrorException e) {
            System.out.println("No rating available.");
        }
        return rating;
    }

    @Override
    public double getAverageRating(String game) {
        double rating = 0.0;
        try {
            rating = restTemplate.getForEntity(url + "/" + game, Double.class).getBody();
        } catch (HttpServerErrorException e) {
            System.out.println("No rating available.");
        }
        return rating;
    }

    @Override
    public void reset() throws RatingException {
        restTemplate.delete(url);
    }
}
