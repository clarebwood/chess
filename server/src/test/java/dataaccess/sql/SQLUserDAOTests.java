package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {

    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseManager.initializeDatabase();
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @AfterEach
    public void clearAll() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    public void createUser() throws DataAccessException {
        UserData user = new UserData(
                "testUser",
                "password123",
                "test@email.com"
        );

        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser(user);

        assertNotNull(retrieved);
        assertEquals(user.username(), retrieved.username());
        assertEquals(user.password(), retrieved.password());
        assertEquals(user.email(), retrieved.email());
    }

    @Test
    public void DuplicateUser() throws DataAccessException {
        UserData user = new UserData(
                "testUser",
                "password123",
                "test@email.com"
        );

        userDAO.createUser(user);

        assertThrows(DataAccessException.class,
                () -> userDAO.createUser(user));
    }

    @Test
    public void getExistingUser() throws DataAccessException {
        UserData user = new UserData(
                "alice",
                "password123",
                "alice@email.com"
        );

        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser(
                new UserData("alice", null, null)
        );

        assertNotNull(retrieved);
        assertEquals("alice", retrieved.username());
        assertEquals("password123", retrieved.password());
        assertEquals("alice@email.com", retrieved.email());
    }

    @Test
    public void getNonexistentUser() throws DataAccessException {
        UserData user = new UserData(
                "nonexistent",
                null,
                null
        );

        assertNull(userDAO.getUser(user));
    }

    @Test
    public void clearUsers() throws DataAccessException {
        UserData user1 = new UserData(
                "alice",
                "password123",
                "alice@email.com"
        );

        UserData user2 = new UserData(
                "bob",
                "password456",
                "bob@email.com"
        );

        userDAO.createUser(user1);
        userDAO.createUser(user2);

        userDAO.clear();

        assertNull(userDAO.getUser(user1));
        assertNull(userDAO.getUser(user2));
    }
}
