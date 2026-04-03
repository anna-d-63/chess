package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import services.GameService;
import services.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    Gson serializer = new Gson();
    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        int gameID = -1;
        Session session = ctx.session;
        UserGameCommand command = new UserGameCommand(CONNECT, "", gameID, ChessGame.TeamColor.BLACK);

        try {
            command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.getGameID();
            String username = getUsername(command.getAuthToken());
            connectionManager.saveSession(gameID, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        } catch (UnauthorizedResponse e) {
            String errorMessage = serializer.toJson(new ErrorMessage("Error: unauthorized"));
            connectionManager.broadcast(command, session, errorMessage, ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = serializer.toJson(new ErrorMessage("Error: " + e.getMessage()));
            connectionManager.broadcast(command, session, errorMessage, ERROR);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, UserGameCommand command) throws Exception {
        connectionManager.add(command.getGameID(), session);

        NotificationMessage notificationMessage = notifyEm(username, command.getColor());
        String message = serializer.toJson(notificationMessage);
        connectionManager.broadcast(command, session, message, NOTIFICATION);

        GameData gameData = gameService.getGame(command.getAuthToken(), command.getGameID());
        String game_message = serializer.toJson(new LoadGameMessage(serializer.toJson(gameData.game()), command.getColor()));
        connectionManager.broadcast(command, session, game_message, LOAD_GAME);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {}

    private void leaveGame(Session session, String username, UserGameCommand command) {}

    private void resign(Session session, String username, UserGameCommand command) {}

    private String getUsername(String authToken) throws DataAccessException {
        return userService.getAuthData(authToken).username();
    }

    private NotificationMessage notifyEm(String username, ChessGame.TeamColor color) {
        String message;
        if (color != null) {
            message = String.format("%s entered the game as %s", username, color);
        } else {
            message = String.format("%s entered the game as an observer", username);
        }
        return new NotificationMessage(message);
    }

}
