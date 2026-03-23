package client;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.requestandresult.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final int port;
    private final Gson serializer = new Gson();

    public ServerFacade(int port) {this.port = port;}

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        var req = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(req);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        var req = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(req);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest, String authToken) throws DataAccessException {
        var req = buildRequest("DELETE", "/session", logoutRequest, authToken);
        var response = sendRequest(req);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        var req = buildRequest("POST", "/game", request, authToken);
        var response = sendRequest(req);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws DataAccessException {
        var req = buildRequest("PUT", "/game", joinGameRequest, authToken);
        var response = sendRequest(req);
        handleResponse(response, null);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest, String authToken) throws DataAccessException {
        var req = buildRequest("GET", "/game", listGamesRequest, authToken);
        var response = sendRequest(req);
        return handleResponse(response, ListGamesResult.class);
    }

    public void clear() throws DataAccessException {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(serializer.toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws DataAccessException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            throw new DataAccessException(body.toString());
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
