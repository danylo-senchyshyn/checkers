package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Comment;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
public class CommentServiceJDBC implements CommentService {
    public static final String SELECT = "SELECT * FROM comment WHERE game = ? ORDER BY commentedOn DESC LIMIT 10";
    public static final String INSERT = "INSERT INTO comment (game, player, comment, commentedOn) VALUES (?, ?, ?, ?)";
    public static final String DELETE = "DELETE FROM comment";

    @Autowired
    private DataSource dataSource;

    @Override
    public void addComment(Comment comment) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, comment.getGame());
            statement.setString(2, comment.getPlayer());
            statement.setString(3, comment.getComment());
            statement.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new CommentException("Problem inserting comment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Comment> comments = new ArrayList<>();
                while (rs.next()) {
                    comments.add(new Comment(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getTimestamp(4)
                    ));
                }
                return comments;
            }
        } catch (SQLException e) {
            throw new CommentException("Problem selecting comment", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new CommentException("Problem deleting comment", e);
        }
    }
}