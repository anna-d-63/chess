package ui;

import chess.ChessGame;
import client.ServerFacade;
import dataaccess.DataAccessException;
import model.GameData;
import server.requestandresult.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class PostLoginUI implements ClientUI {

    private final ServerFacade facade;
    public String authToken = null;
    private final HashMap<Integer, GameData> listedGames = new HashMap<>();
    GameData gameData = null;
    public ChessGame.TeamColor color = WHITE;

    PostLoginUI(int port) {
        facade = new ServerFacade(port);
    }

    @Override
    public String eval(String line) {
        try {
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "create" -> createNewGame(params);
                case "list" -> listAllGames();
                case "join" -> joinAGame(params);
                case "observe" -> observeAGame(params);
                case "logout" -> logoutClient();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String createNewGame(String[] params) throws DataAccessException {
        if (params.length >= 1) {
            var gameName = String.join("-", params);
            var createGameRequest = new CreateGameRequest(authToken, gameName);
            facade.createGame(createGameRequest, authToken);
            return String.format("You created a game named %s", gameName);
        }
        throw new DataAccessException("Expected: <NAME>");
    }

    private String listAllGames() throws DataAccessException {
        var listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest, authToken);
        Collection<GameData> games = listGamesResult.games();
        StringBuilder sb = new StringBuilder();
        int counter = 1;
        listedGames.clear();
        for (GameData game : games) {
            sb.append(counter).append(". ").append(game.gameName())
                    .append("- white: ").append(game.whiteUsername())
                    .append(" black: ").append(game.blackUsername())
                    .append("\n");
            listedGames.put(counter, game);
            counter++;
        }
        return sb.toString();
    }

    private String joinAGame(String[] params) throws DataAccessException {
        if (params.length == 2) {
            if (listedGames.isEmpty()) {throw new DataAccessException("List games to see available IDs");}
            int counter = Integer.parseInt(params[0]);
            int gameID = listedGames.get(counter).gameID();
            var joinGameRequest = new JoinGameRequest(authToken, params[1], gameID);
            facade.joinGame(joinGameRequest, authToken);
            gameData = listedGames.get(counter);
            if (params[1].equalsIgnoreCase("black")){color = BLACK;}
            return String.format("You are playing game: %s", gameData.gameName());
        }
        throw new DataAccessException("Expected: <ID> [WHITE|BLACK]");
    }

    private String observeAGame(String[] params) throws DataAccessException {
        if (params.length == 1) {
            if (listedGames.isEmpty()) {throw new DataAccessException("List games to see available IDs");}
            int counter = Integer.parseInt(params[0]);
            gameData = listedGames.get(counter);
            return String.format("You are observing game: %s", gameData.gameName());
        }
        throw new DataAccessException("Expected: <ID>");
    }

    private String logoutClient() throws DataAccessException {
        var logoutRequest = new LogoutRequest(authToken);
        facade.logout(logoutRequest, authToken);
        authToken = null;
        return "Logged out";
    }

    @Override
    public String firstLine() {
        return SET_TEXT_COLOR_MAGENTA + "Execute any of the following commands for chess play";
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE +
                """
                create <NAME> - create a game
                list - list all games
                join <ID> [WHITE|BLACK] - join a game with specified ID as specified team color
                observe <ID> - observe a game with specified ID
                logout - logout when done
                quit - leave the application
                help - view this menu again
                """;
    }

    @Override
    public boolean readyToBreak() {
        return gameData != null;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
