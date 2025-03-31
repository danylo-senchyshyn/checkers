package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Score;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreServiceJDBC implements ScoreService {
    private static final String INSERT_STATEMENT = "INSERT INTO score (game, player, points, playedOn) VALUES (?, ?, ?, ?)";
    private static final String SELECT_STATEMENT = "SELECT * FROM score WHERE game = ? ORDER BY points DESC LIMIT 10";
    private static final String DELETE_STATEMENT = "DELETE FROM score";

    @Autowired
    private DataSource dataSource;

    @Override
    public void addScore(Score score) {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(INSERT_STATEMENT)
        ) {
            statement.setString(1, score.getGame());
            statement.setString(2, score.getPlayer());
            statement.setInt(3, score.getPoints());
            statement.setDate(4, new Date(score.getPlayedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }

    @Override
    public List<Score> getTopScores(String game) {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(SELECT_STATEMENT)
        ) {
            statement.setString(1, game);
            try (var rs = statement.executeQuery()) {
                var scores = new ArrayList<Score>();
                while (rs.next())
                    scores.add(new Score(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getTimestamp(4)));
                return scores;
            }
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }

    @Override
    public void reset() {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE_STATEMENT);
        } catch (SQLException e) {
            throw new GameStudioException(e);
        }
    }
}
