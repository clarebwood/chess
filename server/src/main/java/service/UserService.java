package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthResult register(RegisterRequest request)
            throws DataAccessException {

        if (request == null || request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Invalid user information");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());

        if (userDAO.getUser(user) != null) {
            throw new DataAccessException("Username taken");
        }

        userDAO.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        authDAO.createAuth(auth);

        return new AuthResult(user.username(), authToken);
    }


    public AuthResult login(LoginRequest request)
            throws DataAccessException {

        if (request == null || request.username() == null || request.password() == null) {
            throw new DataAccessException("Invalid login information");
        }

        UserData user = new UserData(request.username(), request.password(), null);

        UserData storedUser = userDAO.getUser(user);

        if (storedUser == null || !storedUser.password().equals(request.password())) {
            throw new DataAccessException("Invalid username or password");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, storedUser.username());
        authDAO.createAuth(auth);

        return new AuthResult(storedUser.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }

}
