package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);

        authDAO.createAuth(new AuthData("valid-token", "testUser"));
    }

    @Test
    void createGameSuccessfully() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("Test Game");

        int gameID = gameService.createGame("valid-token", request);

        assertTrue(gameID > 0);
        assertNotNull(gameDAO.getGame(gameID));
        assertEquals("Test Game", gameDAO.getGame(gameID).gameName());
    }

    @Test
    void createGameWithoutAuthentication() {
        CreateGameRequest request = new CreateGameRequest("Test Game");

        assertThrows(DataAccessException.class, () -> gameService.createGame("invalid-token", request));
    }


    @Test
    void listGamesSuccessfully() throws DataAccessException {
        gameDAO.createGame("Game One");
        gameDAO.createGame("Game Two");

        Collection<GameData> games = gameService.listGames("valid-token");

        assertEquals(2, games.size());
    }

    @Test
    void listGamesWithoutAuthentication() {
        assertThrows(DataAccessException.class, () -> gameService.listGames("invalid-token"));
    }

    @Test
    void joinGameAsWhite() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");
        JoinGameRequest request = new JoinGameRequest(gameID, "WHITE");

        gameService.joinGame("valid-token", request);

        GameData game = gameDAO.getGame(gameID);
        assertEquals("testUser", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameAsBlack() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");
        JoinGameRequest request = new JoinGameRequest(gameID, "BLACK");

        gameService.joinGame("valid-token", request);

        GameData game = gameDAO.getGame(gameID);
        assertNull(game.whiteUsername());
        assertEquals("testUser", game.blackUsername());
    }

    @Test
    void joinGameWithInvalidColor() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");
        JoinGameRequest request = new JoinGameRequest(gameID, "GREEN");

        assertThrows(DataAccessException.class, () -> gameService.joinGame("valid-token", request));
    }

    @Test
    void joinGameThatDoesNotExist() {
        JoinGameRequest request = new JoinGameRequest(999, "WHITE");

        assertThrows(DataAccessException.class, () -> gameService.joinGame("valid-token", request));
    }

    @Test
    void joinGameWithOccupiedSlot() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");

        gameService.joinGame("valid-token", new JoinGameRequest(gameID, "WHITE"));

        assertThrows(DataAccessException.class, () -> gameService.joinGame("valid-token", new JoinGameRequest(gameID, "WHITE")));
    }

    @Test
    void joinGameWithoutAuthentication() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");
        JoinGameRequest request = new JoinGameRequest(gameID, "WHITE");

        assertThrows(DataAccessException.class, () -> gameService.joinGame("invalid-token", request));
    }
}
