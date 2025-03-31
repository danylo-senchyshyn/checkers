package sk.tuke.gamestudio.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.gamestudio.entity.Rating;

import java.util.List;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) {
        entityManager.persist(rating);
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            return entityManager.createQuery("SELECT COALESCE(r.rating, 0) FROM Rating r WHERE r.game = :game AND r.player = :player", Integer.class)
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RatingException("Problem getting rating", e);
        }
    }

    @Override
    public double getAverageRating(String game) throws RatingException {
        try {
            List<Double> results = entityManager.createQuery("SELECT AVG(r.rating) FROM Rating r WHERE r.game = :game", Double.class)
                    .setParameter("game", game)
                    .getResultList();
            if (results.isEmpty() || results.get(0) == null) {
                return 0.0;
            }
            return results.get(0);
        } catch (Exception e) {
            throw new RatingException("Problem getting average rating", e);
        }
    }

    @Override
    public void reset() {
        entityManager.createQuery("DELETE FROM Rating").executeUpdate();
    }
}