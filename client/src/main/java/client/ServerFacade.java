package client;

import com.google.gson.Gson;
import model.GameData;
import service.AuthResult;
import service.CreateGameRequest;
import service.ListGamesResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private final String serverURL;
    private final HttpClient client;
    private final Gson gson;

    public ServerFacade (int port) {
        this.serverURL = "http://localhost:" + port;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    private String request(String method, String path, String authToken, Object requestBody)
            throws ServerException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(serverURL + path));

        if (authToken != null) {
            builder.header("Authorization", authToken);
        }
        if (requestBody != null) {
            String json = gson.toJson(requestBody);

            builder.header("Content-Type", "application/json");
            builder.method(method, HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        try {
            HttpResponse<String> response = client.send( builder.build(), HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status < 200 || status >= 300) {
                throw new ServerException("Request Failed" + status);
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ServerException("Unable to connect to chess server.", e);
        }

    }

    public AuthResult register (String username, String password, String email) throws ServerException {
        var body = Map.of("username", username, "password", password, "email", email);
        String response = request("POST", "/user", null, body);

        return gson.fromJson(response, AuthResult.class);
    }

    public AuthResult login (String username, String password) throws ServerException {
        var body = Map.of("username", username, "password", password);
        var response = request("POST", "/session", null, body);

        return gson.fromJson(response, AuthResult.class);
    }

    public void logout (String authToken) throws ServerException {
        request("DELETE", "/session", authToken, null);
    }

    public Collection<GameData> listGames(String authToken) throws ServerException {
        var response = request("GET", "/game", authToken, null);
        ListGamesResult result = gson.fromJson(response, ListGamesResult.class);

        return result.games();
    }

    private record CreateGameResult(int gameID) {}

    public int createGame(String authToken, String gameName) throws ServerException {
        CreateGameRequest request = new CreateGameRequest(gameName);
        String response = request("POST", "/game", authToken, request);

        CreateGameResult result = gson.fromJson(response, CreateGameResult.class);
        return result.gameID();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ServerException {
        var body = Map.of("gameID", gameID, "playerColor", playerColor);
        request("PUT", "/game", authToken, body);
    }

    public void observeGame(String authToken, int gameID) throws ServerException {
        Map<String, Object> body = new java.util.HashMap<>();

        body.put("gameID", gameID);
        body.put("playerColor", null);

        request("PUT", "/game", authToken, body);
    }
}
