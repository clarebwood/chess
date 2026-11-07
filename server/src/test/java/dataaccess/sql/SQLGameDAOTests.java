package dataaccess.sql;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseManager.initializeDatabase();
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @AfterEach
    public void clearAll() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    public void createGameSuccessfully() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");

        assertTrue(gameID > 0);

        GameData game = gameDAO.getGame(gameID);

        assertNotNull(game);
        assertEquals(gameID, game.gameID());
        assertEquals("Test Game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertNotNull(game.game());
    }

    @Test
    public void getExistingGame() throws DataAccessException {
        int gameID = gameDAO.createGame("Chess Match");

        GameData game = gameDAO.getGame(gameID);

        assertNotNull(game);
        assertEquals("Chess Match", game.gameName());
    }

    @Test
    public void getNonexistentGame() throws DataAccessException {
        GameData game = gameDAO.getGame(999999);

        assertNull(game);
    }

    @Test
    public void listGames() throws DataAccessException {
        gameDAO.createGame("Game One");
        gameDAO.createGame("Game Two");
        gameDAO.createGame("Game Three");

        Collection<GameData> games = gameDAO.listGames();

        assertEquals(3, games.size());
    }

    @Test
    public void listGamesWhenEmpty() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccessfully() throws DataAccessException {
        int gameID = gameDAO.createGame("Original Name");

        GameData original = gameDAO.getGame(gameID);

        GameData updated = new GameData(
                gameID,
                "whitePlayer",
                "blackPlayer",
                "Updated Name",
                original.game()
        );

        gameDAO.updateGame(updated);

        GameData result = gameDAO.getGame(gameID);

        assertNotNull(result);
        assertEquals("Updated Name", result.gameName());
        assertEquals("whitePlayer", result.whiteUsername());
        assertEquals("blackPlayer", result.blackUsername());
    }

    @Test
    public void updateNonexistentGame() {
        GameData game = new GameData(
                999999,
                "whitePlayer",
                "blackPlayer",
                "Missing Game",
                new ChessGame()
        );

        assertThrows(
                DataAccessException.class,
                () -> gameDAO.updateGame(game)
        );
    }

    @Test
    public void clearGames() throws DataAccessException {
        gameDAO.createGame("Game One");
        gameDAO.createGame("Game Two");

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
    }

}
