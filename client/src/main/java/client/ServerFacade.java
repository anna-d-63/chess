package client;

import Exceptions.DataAccessException;
import requestandresult.*;

public class ServerFacade {

    private final ClientCommunicator communicator;

    public ServerFacade(int port) {
        this.communicator = new ClientCommunicator(port);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        var req = communicator.buildRequest("POST", "/user", registerRequest, null);
        var response = communicator.sendRequest(req);
        return communicator.handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        var req = communicator.buildRequest("POST", "/session", loginRequest, null);
        var response = communicator.sendRequest(req);
        return communicator.handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        var req = communicator.buildRequest("DELETE", "/session", logoutRequest, logoutRequest.authToken());
        var response = communicator.sendRequest(req);
        communicator.handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        var req = communicator.buildRequest("POST", "/game", request, request.authToken());
        var response = communicator.sendRequest(req);
        return communicator.handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        var req = communicator.buildRequest("PUT", "/game", joinGameRequest, joinGameRequest.authToken());
        var response = communicator.sendRequest(req);
        communicator.handleResponse(response, null);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        var req = communicator.buildRequest("GET", "/game", listGamesRequest, listGamesRequest.authToken());
        var response = communicator.sendRequest(req);
        return communicator.handleResponse(response, ListGamesResult.class);
    }

    public void clear() throws DataAccessException {
        var request = communicator.buildRequest("DELETE", "/db", null, null);
        communicator.sendRequest(request);
    }

}
