package sk.tuke.gamestudio.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.CommentServiceJPA;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(CommentServiceJPA.class)
public class CommentServiceJPATest {
    @Autowired
    private CommentService commentService;

    @Test
    public void testAddComment() {
        Comment comment = new Comment("checkers", "player1", "Nice game!", new Date());
        commentService.addComment(comment);

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(1, comments.size());
        assertEquals("player1", comments.get(0).getPlayer());
        assertEquals("Nice game!", comments.get(0).getComment());
    }

    @Test
    public void testFindCommentsByGame() {
        commentService.addComment(new Comment("checkers", "player1", "Great!", new Date()));
        commentService.addComment(new Comment("checkers", "player2", "Awesome!", new Date()));
        commentService.addComment(new Comment("chess", "player3", "Hard game", new Date()));

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(2, comments.size());
    }

    @Test
    public void testDeleteComments() {
        commentService.addComment(new Comment("checkers", "player1", "Cool!", new Date()));
        commentService.reset();
        List<Comment> comments = commentService.getComments("checkers");
        assertTrue(comments.isEmpty());
    }
}