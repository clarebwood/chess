package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO{

    private final Gson gson = new Gson();

    @Override
    public int createGame(String gameName) throws DataAccessException {

        String sql = """
                INSERT INTO gameData (whiteUsername, blackUsername, gameName, game)
                VALUES (?, ?, ?, ?)
                """;

        ChessGame game = new ChessGame();
        String gameJson = gson.toJson(game);

        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setNull(1, Types.VARCHAR);
            statement.setNull(2, Types.VARCHAR);
            statement.setString(3, gameName);
            statement.setString(4, gameJson);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new DataAccessException("Failed to create game ID");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create gameID", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = """
                SELECT *
                FROM GameData
                WHERE gameID = ?
                """;
        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.setInt(1, gameID);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String gameJson = resultSet.getString("game");

                    ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                    return new GameData(
                            gameID,
                            whiteUsername,
                            blackUsername,
                            gameName,
                            game
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }

        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM gameData";

        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear games", e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
                UPDATE gameData
                SET whiteUsername = ?, blackUsername = ?, game = ?, gameName = ?
                WHERE gameID = ?
                """;
        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, gson.toJson(game.game()));
            statement.setString(4, game.gameName());
            statement.setInt(5, game.gameID());

            int updatedRows = statement.executeUpdate();
            if(updatedRows == 0) {
                throw new DataAccessException("Game not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesList = new ArrayList<>();

        String sql = "SELECT * from gameData";

        try (var connection = DatabaseManager.getConnection()) {
            var statement = connection.prepareStatement(sql);

            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                String gameJson = resultSet.getString("game");

                ChessGame game = gson.fromJson(gameJson, ChessGame.class);

                GameData gameListData = new GameData(
                        gameID,
                        whiteUsername,
                        blackUsername,
                        gameName,
                        game
                );
                gamesList.add(gameListData);
            }

            return gamesList;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
    }
}
