package sk.tuke.gamestudio.service.comment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.gamestudio.entity.Comment;

import java.util.List;

@Transactional
public class CommentServiceJPA implements CommentService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) {
        try {
            entityManager.persist(comment);
        } catch (Exception e) {
            throw new RuntimeException("Error while adding comment in jpa", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) {
        try {
            return entityManager.createNamedQuery("Comment.getComments", Comment.class)
                    .setParameter("game", game)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error in jpa while fetching comments for game " + game, e);
        }
    }

    @Override
    public void reset() {
        try {
            entityManager.createNamedQuery("Comment.reset").executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error while resetting comments in jpa", e);
        }
    }
}