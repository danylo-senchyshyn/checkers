package sk.tuke.gamestudio.service;



import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.tuke.gamestudio.entity.Comment;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentServiceTest {
    CommentService commentService;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceJDBC();
    }

    @Test
    public void addComment() {
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("checkers", "test-player1", "Test - Nice game!", date));

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(1, comments.size());
        assertEquals("checkers", comments.get(0).getGame());
        assertEquals("test-player1", comments.get(0).getPlayer());
        assertEquals("Test - Nice game!", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());
    }

    @Test
    public void getComments() {
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("checkers", "test-player1", "Test - Nice game!", date));
        commentService.addComment(new Comment("checkers", "test-player2", "Test - Awesome!", date));

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(2, comments.size());

        assertEquals("checkers", comments.get(0).getGame());
        assertEquals("test-player1", comments.get(0).getPlayer());
        assertEquals("Test - Nice game!", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());

        assertEquals("checkers", comments.get(1).getGame());
        assertEquals("test-player2", comments.get(1).getPlayer());
        assertEquals("Test - Awesome!", comments.get(1).getComment());
        assertEquals(date, comments.get(1).getCommentedOn());
    }

    @Test
    public void resetComments() {
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("checkers", "test-player1", "Test - Nice game!", date));

        List<Comment> comments = commentService.getComments("checkers");
        assertEquals(1, comments.size());

        commentService.reset();

        comments = commentService.getComments("checkers");
        assertEquals(0, comments.size());
    }
}