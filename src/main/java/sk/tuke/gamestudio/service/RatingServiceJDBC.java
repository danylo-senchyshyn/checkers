package sk.tuke.gamestudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.tuke.gamestudio.entity.Rating;

import javax.sql.DataSource;
import java.sql.*;

@Service
public class RatingServiceJDBC implements RatingService {
    private static final String checkQuery = "SELECT COUNT(*) FROM rating WHERE game = ? AND player = ?";
    private static final String insertQuery = "INSERT INTO rating (game, player, rating, ratedOn) VALUES (?, ?, ?, ?)";
    private static final String updateQuery = "UPDATE rating SET rating = ?, ratedOn = ? WHERE game = ? AND player = ?";
    private static final String selectRatingQuery = "SELECT rating FROM rating WHERE game = ?";
    private static final String selectUserRatingQuery = "SELECT COALESCE(rating, 0) FROM rating WHERE game = ? AND player = ?";
    private static final String deleteQuery = "DELETE FROM rating";

    @Autowired
    private DataSource dataSource;

    @Override
    public void setRating(Rating rating) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            checkStmt.setString(1, rating.getGame());
            checkStmt.setString(2, rating.getPlayer());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, rating.getRating());
                        updateStmt.setTimestamp(2, new Timestamp(rating.getRatedOn().getTime()));
                        updateStmt.setString(3, rating.getGame());
                        updateStmt.setString(4, rating.getPlayer());
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, rating.getGame());
                        insertStmt.setString(2, rating.getPlayer());
                        insertStmt.setInt(3, rating.getRating());
                        insertStmt.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem inserting/updating rating", e);
        }
    }

    @Override
    public double getAverageRating(String game) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectRatingQuery)) {

            statement.setString(1, game);

            double sum = 0;
            int count = 0;

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    sum += rs.getInt("rating");
                    count++;
                }
            }

            return count > 0 ?
                    Math.round((sum / count) * 10.0) / 10.0
                    :
                    0.0;
        } catch (SQLException e) {
            throw new RatingException("Problem selecting rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectUserRatingQuery)) {

            statement.setString(1, game);
            statement.setString(2, player);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RatingException("Problem selecting rating", e);
        }

        return 0;
    }

    @Override
    public void reset() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting score", e);
        }
    }
}
