package client.ui;

import chess.ChessGame;
import client.ServerFacade;
import client.websocket.ServerMessageObserver;
import client.websocket.WebsocketCommunicator;
import exceptions.DataAccessException;
import model.GameData;
import requestandresult.*;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static client.ui.EscapeSequences.*;
import static client.ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class PostLoginUI implements ClientUI {

    private final ServerFacade facade;
    WebsocketCommunicator ws;
    public String authToken = null;
    GameData gameData = null;
    public ChessGame.TeamColor color = WHITE;
    private final HashMap<Integer, GameData> listedGames = new HashMap<>();

    PostLoginUI(int port, WebsocketCommunicator ws) {
        facade = new ServerFacade(port);
        this.ws = ws;
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
                case "quit" -> quitAndLogout();
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
            facade.createGame(createGameRequest);
            return String.format("You created a game named %s", gameName);
        }
        throw new DataAccessException("Expected: <NAME>");
    }

    private String listAllGames() throws DataAccessException {
        var listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
        if (listGamesResult.games().isEmpty()) {
            throw new DataAccessException("No games available to list");
        }
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
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private String joinAGame(String[] params) throws DataAccessException {
        if (params.length == 2) {
            if (listedGames.isEmpty()) {throw new DataAccessException("List games to see available IDs");}
            int counter;
            try {
                counter = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new DataAccessException("Enter the listed number of the game you want to join \n" +
                        "type 'list' to see available games");
            }
            if (!listedGames.containsKey(counter)) {
                throw new DataAccessException("You must join a game in the list");
            }
            int gameID = listedGames.get(counter).gameID();
            var joinGameRequest = new JoinGameRequest(authToken, params[1].toUpperCase(), gameID);
            facade.joinGame(joinGameRequest);
            gameData = listedGames.get(counter);
            if (params[1].equalsIgnoreCase("black")){color = BLACK;}
            else {color = WHITE;}
            ws.connectToGame(authToken, gameID, color);
            return String.format("You are playing %s", gameData.gameName());
        }
        throw new DataAccessException("Expected: <ID> [WHITE|BLACK]");
    }

    private String observeAGame(String[] params) throws DataAccessException {
        if (params.length == 1) {
            if (listedGames.isEmpty()) {throw new DataAccessException("List games to see available IDs");}
            int counter;
            try {
                counter = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new DataAccessException("Enter the listed number of the game you want to observe \n" +
                        "type 'list' to see available games");
            }
            if (!listedGames.containsKey(counter)) {
                throw new DataAccessException("You must join a game in the list");
            }
            gameData = listedGames.get(counter);
            color = WHITE;
            ws.connectToGame(authToken, gameData.gameID(), null);
            return String.format("You are observing %s", gameData.gameName());
        }
        throw new DataAccessException("Expected: <ID>");
    }

    private String logoutClient() throws DataAccessException {
        var logoutRequest = new LogoutRequest(authToken);
        facade.logout(logoutRequest);
        authToken = null;
        return "Logged out";
    }

    private String quitAndLogout () throws DataAccessException {
        logoutClient();
        authToken = "placeholder";
        return "quit";
    }

    @Override
    public String firstLine() throws DataAccessException {
        gameData = null;
        return SET_TEXT_COLOR_MAGENTA + "Here are the available chess games: \n" +
                SET_TEXT_COLOR_LIGHT_GREY + listAllGames() + "\n" +
                SET_TEXT_COLOR_MAGENTA + "Execute any of the following commands for chess play";
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "create <NAME> " +
                SET_TEXT_COLOR_LIGHT_GREY + "- create a game \n" +
                SET_TEXT_COLOR_BLUE + "list " +
                SET_TEXT_COLOR_LIGHT_GREY + "- list all games \n" +
                SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK] " +
                SET_TEXT_COLOR_LIGHT_GREY + "- join a game with specified ID as specified team color \n" +
                SET_TEXT_COLOR_BLUE + "observe <ID> " +
                SET_TEXT_COLOR_LIGHT_GREY + "- observe a game with specified ID \n" +
                SET_TEXT_COLOR_BLUE + "logout " +
                SET_TEXT_COLOR_LIGHT_GREY + "- logout when done \n" +
                SET_TEXT_COLOR_BLUE + "quit " +
                SET_TEXT_COLOR_LIGHT_GREY + "- leave the application \n" +
                SET_TEXT_COLOR_BLUE + "help " +
                SET_TEXT_COLOR_LIGHT_GREY + "- view this menu again \n";
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    @Override
    public GameData getGameData() {
        return this.gameData;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostLoginUI that = (PostLoginUI) o;
        return Objects.equals(facade, that.facade) && Objects.equals(authToken, that.authToken)
                && Objects.equals(gameData, that.gameData)
                && Objects.equals(listedGames, that.listedGames) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(facade, authToken, gameData, listedGames, color);
    }
}
