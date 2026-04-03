package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import requestandresult.ListGamesRequest;
import services.GameService;
import services.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;
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
            if (command.getCommandType() == MAKE_MOVE) {
                command = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
            }
            gameID = command.getGameID();
            connectionManager.saveSession(gameID, session);
            String username = getUsername(command.getAuthToken());

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
        GameData gameData = gameService.getGame(command.getAuthToken(), command.getGameID());

        NotificationMessage notificationMessage = notifyEm(username, command.getColor());
        String message = serializer.toJson(notificationMessage);
        connectionManager.broadcast(command, session, message, NOTIFICATION);

        String game_message = serializer.toJson(new LoadGameMessage(serializer.toJson(gameData.game()), command.getColor()));
        connectionManager.broadcast(command, session, game_message, LOAD_GAME);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws DataAccessException {
        GameData gameData = gameService.getGame(command.getAuthToken(), command.getGameID());
        testIfTheirTurn(username, command, gameData);
        testIfValidMove(command.getMove(), gameData);
        ChessGame updatedGame = updateGame(gameData, command.getMove());
        storeGame = gameService.setGame(command.getGameID(), updatedGame);
        //send load game message
        //send notification message about what move was made
        //send notification message if check, checkmate, or stalemate. TODO: be able to check for those things
        Collection<ChessMove> validMoves = gameData.game().validMoves(command.getMove().getStartPosition());
        if (!validMoves.contains(command.getMove())){
            throw new DataAccessException("move is not valid");
        }

    }

    private void leaveGame(Session session, String username, UserGameCommand command) {}

    private void resign(Session session, String username, UserGameCommand command) {}

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = userService.getAuthData(authToken);
        if (authData == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        return authData.username();
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
