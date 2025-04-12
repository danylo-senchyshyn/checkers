package sk.tuke.gamestudio.service.comment;

import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.service.gamestudio.GameStudioService;

import java.util.List;

public interface CommentService extends GameStudioService {
    void addComment(Comment comment) throws CommentException;
    List<Comment> getComments(String game) throws CommentException;
    void reset() throws CommentException;
}
