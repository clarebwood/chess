package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import model.GameData;
import model.UserData;

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
                String errorMessage = getError(response.body());
                throw new ServerException(errorMessage);
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ServerException("Unable to connect to chess server.", e);
        }

    }

    private String getError (String body) {
        try {
            JsonObject json = gson.fromJson( body, JsonObject.class );

            if (json != null && json.has("message")) {
                return json.get("message").getAsString();
            }
        } catch (Exception ignored) {}
        return "Unable to complete request";
    }

    public AuthData register (String username, String password, String email) throws ServerException {
        UserData body = new UserData(username, password, email);
        String response = request("POST", "/user", null, body);

        return gson.fromJson(response, AuthData.class);
    }

    public AuthData login (String username, String password) throws ServerException {
        UserData body = new UserData(username, password, null);
        var response = request("POST", "/session", null, body);

        return gson.fromJson(response, AuthData.class);
    }

    public void logout (String authToken) throws ServerException {
        request("DELETE", "/session", authToken, null);
    }

    public Collection<GameData> listGames(String authToken) throws ServerException {
        String response = request("GET", "/game", authToken, null);
        JsonObject json = gson.fromJson(response, JsonObject.class);

        GameData[] games = gson.fromJson(json.get("games"), GameData[].class);

        return new java.util.ArrayList<>(java.util.Arrays.asList(games));
    }

    private record CreateGameResult(int gameID) {}

    public int createGame(String authToken, String gameName) throws ServerException {
        Map<String, String> body = Map.of("gameName", gameName);
        String response = request("POST", "/game", authToken, body);

        JsonObject json = gson.fromJson(response, JsonObject.class);
        return json.get("gameID").getAsInt();
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
