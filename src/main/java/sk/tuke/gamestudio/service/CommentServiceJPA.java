package sk.tuke.gamestudio.service;

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
            entityManager.persist(comment);
        }

        @Override
        public List<Comment> getComments(String game) {
            return entityManager.createQuery("SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.commentedOn DESC", Comment.class)
                    .setParameter("game", game)
                    .getResultList();
        }

        @Override
        public void reset() {
            entityManager.createNativeQuery("DELETE FROM Comment").executeUpdate();
        }
    }