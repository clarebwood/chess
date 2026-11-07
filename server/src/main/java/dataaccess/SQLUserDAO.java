package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    @Override
    public void createUser(UserData u) throws DataAccessException {
        String sql = """
                INSERT INTO userData (username, password, email)
                VALUES (?, ?, ?)
                """;
        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.setString(1, u.username());
            statement.setString(2, u.password());
            statement.setString(3, u.email());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user", e);
        }
    }

    @Override
    public UserData getUser(UserData user) throws DataAccessException {
        String sql = """
                SELECT username, password, email
                FROM userData
                WHERE username = ?
                """;
        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.setString(1, user.username());

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UserData(resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get user", e);
        }

        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM userData";

        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear users", e);
        }
    }
}
