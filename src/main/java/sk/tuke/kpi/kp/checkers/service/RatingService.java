package sk.tuke.kpi.kp.checkers.service;

import sk.tuke.kpi.kp.checkers.entity.Rating;

import java.util.List;

public interface RatingService {
    void setRating(Rating rating) throws RatingException;
    int getAverageRating(String game) throws RatingException;
    int getRating(String game, String player) throws RatingException;
    void reset() throws RatingException;
}
