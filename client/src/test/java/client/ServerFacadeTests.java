import client.ServerException;
import dataaccess.DataAccessException;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void beforeEach() throws DataAccessException {
        var userDAO = new SQLUserDAO();
        var authDAO = new SQLAuthDAO();
        var gameDAO = new SQLGameDAO();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void registerPositive() throws Exception {
        var auth = facade.register("player1", "password123", "player1@email.com");

        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertFalse(auth.authToken().isEmpty());
        assertEquals("player1", auth.username());
    }

    @Test
    void registerDuplicate() throws Exception {
        facade.register("player1", "password123", "player1@email.com");

        assertThrows(ServerException.class, () ->
                facade.register("player1", "anotherPassword", "another@email.com")
        );
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("player1", "password123", "player1@email.com");

        var auth = facade.login("player1", "password123");

        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertFalse(auth.authToken().isEmpty());
        assertEquals("player1", auth.username());
    }

    @Test
    void loginNonexistent() {
        assertThrows(ServerException.class, () ->
                facade.login("unknown", "password123")
        );
    }

    @Test
    void loginWrongPassword() throws Exception {
        facade.register("player1", "password123", "player1@email.com");

        assertThrows(ServerException.class, () ->
                facade.login("player1", "wrongPassword")
        );
    }

    @Test
    void logoutPositive() throws Exception {
        var auth = facade.register("player1", "password123", "player1@email.com");

        assertDoesNotThrow(() ->
                facade.logout(auth.authToken())
        );
    }

    @Test
    void logoutNegative() {
        assertThrows(ServerException.class, () ->
                facade.logout("invalid-token")
        );
    }

    @Test
    void listGamesPositive() throws Exception {
        var auth = facade.register("player1", "password123", "player1@email.com");

        facade.createGame(auth.authToken(), "Chess Game");

        Collection<GameData> games = facade.listGames(auth.authToken());

        assertNotNull(games);
        assertEquals(1, games.size());
    }

    @Test
    void listGamesNegative() {
        assertThrows(ServerException.class, () ->
                facade.listGames("invalid-token")
        );
    }

    @Test
    void listGamesEmpty() throws Exception {
        var auth = facade.register("player1", "password123", "player1@email.com");

        Collection<GameData> games = facade.listGames(auth.authToken());

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void createGamePositive() throws Exception {
        var auth = facade.register(
                "player1", "password123", "player1@email.com"
        );

        int gameID = facade.createGame(
                auth.authToken(), "My Chess Game"
        );

        assertTrue(gameID > 0);
    }

    @Test
    void createGameNegative() {
        assertThrows(ServerException.class, () ->
                facade.createGame("invalid-token", "Chess Game")
        );
    }

    @Test
    void joinGamePositive() throws Exception {
        var auth = facade.register(
                "player1", "password123", "player1@email.com"
        );

        int gameID = facade.createGame(
                auth.authToken(), "Chess Game"
        );

        assertDoesNotThrow(() ->
                facade.joinGame(
                        auth.authToken(), "WHITE", gameID
                )
        );
    }

    @Test
    void joinGameNegative() throws Exception {
        var auth = facade.register(
                "player1", "password123", "player1@email.com"
        );

        assertThrows(ServerException.class, () ->
                facade.joinGame(
                        auth.authToken(), "WHITE", 999999
                )
        );
    }

    @Test
    void joinGameBlack() throws Exception {
        var auth = facade.register(
                "player1", "password123", "player1@email.com"
        );

        var auth2 = facade.register(
                "player2", "password456", "player2@email.com"
        );

        int gameID = facade.createGame(
                auth.authToken(), "Chess Game"
        );

        assertDoesNotThrow(() ->
                facade.joinGame(
                        auth2.authToken(), "BLACK", gameID
                )
        );
    }

    @Test
    void joinGameInvalid() throws Exception {
        var auth = facade.register(
                "player1", "password123", "player1@email.com"
        );

        int gameID = facade.createGame(
                auth.authToken(), "Chess Game"
        );

        assertThrows(ServerException.class, () ->
                facade.joinGame(
                        "invalid-token", "WHITE", gameID
                )
        );
    }

    @Test
    void observeGamePositive() throws Exception {
        var auth = facade.register("player1", "password123", "player1@email.com"
        );

        int gameID = facade.createGame(auth.authToken(), "Chess Game");

        var games = facade.listGames(auth.authToken());

        assertTrue(games.stream().anyMatch(game ->game.gameID() == gameID));
    }

    @Test
    void observeGameNegative() throws Exception {
        var auth = facade.register(
                "player1", "password123", "player1@email.com"
        );

        assertThrows(ServerException.class, () ->
                facade.observeGame(auth.authToken(), 999999)
        );
    }

    @Test
    void observeGameInvalid() throws Exception {
        var auth = facade.register(
                "player1", "password123", "player1@email.com"
        );

        int gameID = facade.createGame(
                auth.authToken(), "Chess Game"
        );

        assertThrows(ServerException.class, () ->
                facade.observeGame("invalid-token", gameID)
        );
    }
}
