package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.GameData;
import service.*;

import java.util.Collection;
import java.util.Map;

public class Server {
    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clearDb)
                .post("/user", this::register)
                .post("/session", this::logIn)
                .delete("/session", this::logOut)
                .get("/game", this::getGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame);

        javalin.exception(DataAccessException.class, (e, ctx) -> {
            sendError(ctx, e);
        });

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: Internal server error"));
        });
    }

    private void register(Context ctx) throws DataAccessException {
        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);

        AuthResult result = userService.register(request);

        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(result));
    }

    private void logIn(Context ctx) throws DataAccessException {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

        AuthResult result = userService.login(request);

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void logOut(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        userService.logout(authToken);

        ctx.status(200);
        ctx.result("{}");
    }

    private void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);

        int gameID = gameService.createGame(authToken, request);

        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("gameID", gameID)));
    }

    private void getGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        Collection<GameData> games = gameService.listGames(authToken);

        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(new ListGamesResult(games)));
    }

    private void joinGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);

        gameService.joinGame(authToken, request);

        ctx.status(200);
        ctx.result("{}");
    }

    private void clearDb(Context ctx) throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        ctx.status(200);
        ctx.result("{}");
    }

    private void sendError(Context ctx, DataAccessException ex) {
        String message = ex.getMessage();
        int status = 500;

        if (message != null) {
            if (message.equals("Unauthorized") || message.equals("Invalid username or password")) {
                status = 401;
            } else if (message.equals("Username taken") || message.contains("spot is taken")) {
                status = 403;
            } else if (message.startsWith("Invalid") || message.equals("Game not found")) {
                status = 400;
            }
        }

        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of(
                "message", "Error: " +
                        (message == null
                                ? "Internal server error"
                                : message)
        )));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
