package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "3243";

    public static final String DELETE = "DELETE FROM rating";

    @Override
    public void setRating(Rating rating) {
        String checkQuery = "SELECT COUNT(*) FROM rating WHERE game = ? AND player = ?";
        String insertQuery = "INSERT INTO rating (game, player, rating, ratedOn) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE rating SET rating = ?, ratedOn = ? WHERE game = ? AND player = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
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
        String query = "SELECT rating FROM rating WHERE game = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, game);

            double sum = 0;
            int count = 0;

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    sum += rs.getInt("rating");
                    count++;
                }
            }

            return count > 0 ? Math.round((sum / count) * 10.0) / 10.0 : 0.0;
        } catch (SQLException e) {
            throw new RatingException("Problem selecting rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) {
        String query = "SELECT COALESCE(rating, 0) FROM rating WHERE game = ? AND player = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

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
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting score", e);
        }
    }
}
