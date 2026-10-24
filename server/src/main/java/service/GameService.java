package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String authToken, CreateGameRequest request) throws DataAccessException {
        if (getAuthUser(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }

        if (request == null || request.gameName() == null) {
            throw new DataAccessException("Invalid game name");
        }

        return gameDAO.createGame(request.gameName());
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        if (getAuthUser(authToken) == null) {
            throw new DataAccessException("Unauthorized");
        }

        return gameDAO.listGames();
    }

    private AuthData getAuthUser(String authToken)
            throws DataAccessException {

        if (authToken == null) {
            return null;
        }

        return authDAO.getAuth(authToken);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        AuthData auth = getAuthUser(authToken);

        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }

        if (request == null) {
            throw new DataAccessException("Invalid join request");
        }

        GameData game = gameDAO.getGame(request.gameID());

        if (game == null) {
            throw new DataAccessException("Game not found");
        }

        String playerColor = request.playerColor();

        if (playerColor == null || (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new DataAccessException("Invalid player color");
        }

        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("White player spot is taken");
            }

            GameData updatedGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());

            gameDAO.updateGame(updatedGame);
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Black player spot is taken");
            }

            GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());

            gameDAO.updateGame(updatedGame);
        }
    }

}
