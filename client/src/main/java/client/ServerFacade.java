package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
}
