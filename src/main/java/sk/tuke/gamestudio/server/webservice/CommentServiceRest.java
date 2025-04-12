package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.service.comment.CommentException;
import sk.tuke.gamestudio.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentServiceRest {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public void addComment(@RequestBody Comment comment) throws CommentException {
        commentService.addComment(comment);
    }

    @GetMapping("/{game}")
    public List<Comment> getComments(@PathVariable String game) throws CommentException {
        return commentService.getComments(game);
    }

    @DeleteMapping
    public void reset() throws CommentException {
        commentService.reset();
    }
}
