package sk.tuke.gamestudio.service.rating;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import sk.tuke.gamestudio.entity.Rating;

import java.util.List;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) {
        try {
            int ratingId = ((Rating) entityManager.createQuery("SELECT r FROM Rating r WHERE r.game=:game AND r.player=:player")
                    .setParameter("player", rating.getPlayer())
                    .setParameter("game", rating.getGame())
                    .getSingleResult())
                    .getIdent();

            Rating existingRating = entityManager.getReference(Rating.class, ratingId);
            existingRating.setRating(rating.getRating());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            entityManager.persist(rating);
        }

    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            List<Rating> ratings = entityManager.createNamedQuery("Rating.getRating", Rating.class)
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getResultList();
            if (ratings.isEmpty()) {
                return 0;
            }
            return ratings.get(0).getRating();
        } catch (Exception e) {
            throw new RatingException("Problem getting rating", e);
        }
    }

    @Override
    public double getAverageRating(String game) throws RatingException {
        try {
            Double averageRating = (Double) entityManager.createNamedQuery("Rating.getAverageRating")
                    .setParameter("game", game)
                    .getSingleResult();
            return averageRating != null ?
                    Math.round(averageRating * 10.0) / 10.0
                    :
                    0.0;
        } catch (Exception e) {
            throw new RatingException("Problem getting average rating", e);
        }
    }

    @Override
    public void reset() {
        try {
            entityManager.createNamedQuery("Rating.reset").executeUpdate();
        } catch (Exception e) {
            throw new RatingException("Problem resetting ratings", e);
        }
    }
}