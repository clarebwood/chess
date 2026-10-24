package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthResult;
import service.LoginRequest;
import service.RegisterRequest;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerUserSuccessfully() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@email.com");

        AuthResult result = userService.register(request);

        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());
    }

    @Test
    void registerDuplicateUsername() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@email.com");

        userService.register(request);

        assertThrows(DataAccessException.class, () -> userService.register(request));
    }

    @Test
    void loginSuccessfully() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@email.com");

        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "password");

        AuthResult result = userService.login(loginRequest);

        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isBlank());
    }

    @Test
    void loginWithWrongPassword() throws DataAccessException {
        userService.register(new RegisterRequest("testUser", "password", "test@email.com"));

        LoginRequest request = new LoginRequest("testUser", "wrongPassword");

        assertThrows(DataAccessException.class, () -> userService.login(request));
    }

    @Test
    void loginInvalidRequest() {
        LoginRequest request = new LoginRequest("", "password");

        assertThrows(DataAccessException.class, () -> userService.login(request));
    }

    @Test
    void logoutSuccessfully() throws DataAccessException {
        AuthResult result = userService.register(new RegisterRequest("testUser", "password", "test@email.com"));

        userService.logout(result.authToken());

        assertThrows(DataAccessException.class, () -> userService.logout(result.authToken()));
    }

    @Test
    void logoutInvalidToken() {
        assertThrows(DataAccessException.class, () -> userService.logout("invalid-token"));
    }
}
