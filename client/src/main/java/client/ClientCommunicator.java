package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientCommunicator {

    private final HttpClient client = HttpClient.newHttpClient();
    private final int port;
    private final Gson serializer = new Gson();

    ClientCommunicator(int port) {this.port = port;}

    public HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    public HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(serializer.toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    public HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
            String errorMessage = obj.get("message").getAsString();
            throw new DataAccessException(errorMessage);
        }
        if (responseClass != null) {
            return serializer.fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status/100 == 2;
    }
}
