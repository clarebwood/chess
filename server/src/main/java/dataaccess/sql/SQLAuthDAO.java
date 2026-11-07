package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

        String sql = "INSERT INTO authData(authToken, username) VALUES (?, ?)";

        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth", e);
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = """
                SELECT authToken, username
                FROM authData
                WHERE authToken = ?
                """;
        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.setString(1, authToken);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AuthData(resultSet.getString("authToken"),
                            resultSet.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get auth", e);
        }

        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM authData";

        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = """
                DELETE FROM authData
                WHERE authToken = ?
                """;

        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth", e);
        }
    }
}
