package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SQLAuthDAOTests {

    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseManager.initializeDatabase();
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @AfterEach
    public void clearAll() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    public void createAuthSuccessfully() throws DataAccessException {
        AuthData auth = new AuthData("token123", "alice");

        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token123");

        assertNotNull(retrieved);
        assertEquals(auth.authToken(), retrieved.authToken());
        assertEquals(auth.username(), retrieved.username());
    }

    @Test
    public void DuplicateAuth() throws DataAccessException {
        AuthData auth = new AuthData("token123", "alice");

        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(auth));
    }

    @Test
    public void getExistingAuth() throws DataAccessException {
        AuthData auth = new AuthData("token456", "bob");

        authDAO.createAuth(auth);

        AuthData retrieved = authDAO.getAuth("token456");

        assertNotNull(retrieved);
        assertEquals("token456", retrieved.authToken());
        assertEquals("bob", retrieved.username());
    }

    @Test
    public void getNonexistentAuth() throws DataAccessException {
        assertNull(authDAO.getAuth("invalidToken"));
    }

    @Test
    public void deleteAuthSuccessfully() throws DataAccessException {
        AuthData auth = new AuthData("token789", "clancy");

        authDAO.createAuth(auth);
        authDAO.deleteAuth("token789");

        assertNull(authDAO.getAuth("token789"));
    }

    @Test
    public void deleteNonexistentAuth() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("invalidToken"));
    }

    @Test
    public void clearAuth() throws DataAccessException {
        authDAO.createAuth(new AuthData("token1", "alice"));
        authDAO.createAuth(new AuthData("token2", "bob"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }

}
