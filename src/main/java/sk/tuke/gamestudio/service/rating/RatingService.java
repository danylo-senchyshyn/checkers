package sk.tuke.gamestudio.service.rating;

import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.gamestudio.GameStudioService;

public interface RatingService extends GameStudioService {
    void setRating(Rating rating) throws RatingException;
    double getAverageRating(String game) throws RatingException;
    int getRating(String game, String player) throws RatingException;
    void reset() throws RatingException;
}
