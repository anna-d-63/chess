package ui;

import chess.ChessGame;
import client.ServerFacade;
import Exceptions.DataAccessException;
import model.GameData;
import requestandresult.LoginRequest;
import requestandresult.LoginResult;
import requestandresult.RegisterRequest;
import requestandresult.RegisterResult;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class PreLoginUI implements ClientUI {

    private final ServerFacade facade;
    public String authToken = null;
    public GameData gameData = null;

    PreLoginUI(int port) throws DataAccessException {
        facade = new ServerFacade(port);
    }

    @Override
    public String eval(String line) {
        try {
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "register" -> registerClient(params);
                case "login" -> loginClient(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String firstLine() {
        authToken = null;
        return SET_TEXT_COLOR_MAGENTA + BLACK_KING +
                "Welcome to Chess. Type a command below to get started."
                + BLACK_QUEEN;
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL> " +
                SET_TEXT_COLOR_LIGHT_GREY + "- create an account \n" +
                SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> " +
                SET_TEXT_COLOR_LIGHT_GREY + "- play chess \n" +
                SET_TEXT_COLOR_BLUE + "quit " +
                SET_TEXT_COLOR_LIGHT_GREY + "- leave the application \n" +
                SET_TEXT_COLOR_BLUE + "help " +
                SET_TEXT_COLOR_LIGHT_GREY + "- view this menu again \n";
    }

    private String registerClient(String[] params) throws DataAccessException {
        if (params.length == 3) {
            var registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult = facade.register(registerRequest);
            authToken = registerResult.authToken();
            return String.format("You are signed in as %s", registerResult.username());
        }
        throw new DataAccessException("Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String loginClient(String[] params) throws DataAccessException {
        if (params.length == 2) {
            var loginRequest = new LoginRequest(params[0], params[1]);
            LoginResult loginResult = facade.login(loginRequest);
            authToken = loginResult.authToken();
            return String.format("You are signed in as %s", loginResult.username());
        }
        throw new DataAccessException("Expected: <USERNAME> <PASSWORD>");
    }

    public String getAuthToken() {
        return this.authToken;
    }

    @Override
    public GameData getGameData() {
        return this.gameData;
    }

    public ChessGame.TeamColor getColor() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreLoginUI that = (PreLoginUI) o;
        return Objects.equals(facade, that.facade) && Objects.equals(authToken, that.authToken) && Objects.equals(gameData, that.gameData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facade, authToken, gameData);
    }
}
