package sk.tuke.gamestudio.service;

import org.junit.Before;
import org.junit.Test;
import sk.tuke.kpi.kp.entity.Comment;
import sk.tuke.kpi.kp.service.CommentService;
import sk.tuke.kpi.kp.service.CommentServiceJDBC;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommentServiceTest {
    CommentService commentService;

    @Before
    public void setUp() {
        commentService = new CommentServiceJDBC();
    }

    @Test
    public void addComment() {
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("checkers", "player1", "Nice game!", date));

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(1, comments.size());
        assertEquals("checkers", comments.get(0).getGame());
        assertEquals("player1", comments.get(0).getPlayer());
        assertEquals("Nice game!", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());

        commentService.reset();
    }

    @Test
    public void getComments() {
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("checkers", "player1", "Nice game!", date));
        commentService.addComment(new Comment("checkers", "player2", "Awesome!", date));

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(2, comments.size());

        assertEquals("checkers", comments.get(0).getGame());
        assertEquals("player1", comments.get(0).getPlayer());
        assertEquals("Nice game!", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());

        assertEquals("checkers", comments.get(1).getGame());
        assertEquals("player2", comments.get(1).getPlayer());
        assertEquals("Awesome!", comments.get(1).getComment());
        assertEquals(date, comments.get(1).getCommentedOn());

        commentService.reset();
    }

    @Test
    public void resetComments() {
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("checkers", "player1", "Nice game!", date));

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(1, comments.size());

        commentService.reset();

        comments = commentService.getComments("checkers");
        assertEquals(0, comments.size());

        commentService.reset();
    }
}