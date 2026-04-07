package server.websocket;

import chess.*;
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

import java.io.IOException;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
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
        UserGameCommand command = new UserGameCommand(CONNECT, "", gameID, BLACK);

        try {
            command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            if (command.getCommandType() == MAKE_MOVE) {
                command = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
            }
            gameID = command.getGameID();
            connectionManager.saveSession(gameID, session);
            String username = getUsername(command.getAuthToken());
            setColor(command, username, gameID, command.getAuthToken());

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

    private void makeMove(Session session, String username, MakeMoveCommand command)
            throws DataAccessException, InvalidMoveException, IOException {

        GameData gameData = gameService.getGame(command.getAuthToken(), command.getGameID());
        testIfTheirTurn(command, gameData);
        testIfValidMove(command.getMove(), gameData);
        ChessGame updatedGame = updateGame(command.getMove(), gameData);
        gameService.setGame(command.getGameID(), updatedGame);

        String game_message = serializer.toJson(new LoadGameMessage(serializer.toJson(updatedGame), command.getColor()));
        connectionManager.broadcast(command, session, game_message, LOAD_GAME);

        String formattedMove = formatMove(command.getMove());
        var message = serializer.toJson(new NotificationMessage(String.format("%s moved %s", username, formattedMove)));
        connectionManager.broadcast(command, session, message, NOTIFICATION);


        ChessGame.TeamColor otherColor;
        if (command.getColor() == WHITE) {
            otherColor = BLACK;
        } else {otherColor = WHITE;}
        //TODO: get other username using otherColor and gameData

        if (updatedGame.isInCheck(otherColor) && !updatedGame.isInCheckmate(otherColor)) {
            var checkMessage =
                    serializer.toJson(new NotificationMessage(String.format("%s is in check", "otherUsername")));
            connectionManager.broadcast(command, session, checkMessage, NOTIFICATION);
        } else if (updatedGame.isInCheckmate(otherColor)) {
            var checkmateMessage =
                    serializer.toJson(new NotificationMessage(String.format("%s is in checkmate", "otherUsername")));
            connectionManager.broadcast(command, session, checkmateMessage, NOTIFICATION);
            //allow no more moves to be made
        } else if (updatedGame.isInStalemate(otherColor)) {
            var stalemateMessage =
                    serializer.toJson(new NotificationMessage("stalemate, bummer"));
            connectionManager.broadcast(command, session, stalemateMessage, NOTIFICATION);
            //allow no more moves to be made
        }
        //TODO: do everything on the client side for make move
    }

    private void leaveGame(Session session, String username, UserGameCommand command) {}

    private void resign(Session session, String username, UserGameCommand command) {}

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = userService.getAuthData(authToken);
        if (authData == null) {
            throw new UnauthorizedResponse("Error: unauthorized");
        }
        return authData.username();
    }

    private void setColor(UserGameCommand command, String username, int gameID, String authToken) throws DataAccessException {
        if (command.getColor() != null) {return;}
        GameData gameData = gameService.getGame(authToken, gameID);
        if (gameData.whiteUsername().equals(username)) {
            command.setColor(WHITE);
        } else if (gameData.blackUsername().equals(username)) {
            command.setColor(BLACK);
        }
    }

    private void testIfTheirTurn(MakeMoveCommand command, GameData gameData) throws DataAccessException {
        ChessGame.TeamColor theirColor = command.getColor();
        ChessGame game = gameData.game();
        ChessGame.TeamColor teamTurn = game.getTeamTurn();
        if (theirColor != teamTurn) {
            throw new DataAccessException("can only make a move on your turn.");
        }
    }

    private void testIfValidMove(ChessMove move, GameData gameData) throws DataAccessException {
        ChessGame game = gameData.game();
        ChessPosition startPos = move.getStartPosition();
        Collection<ChessMove> validMoves = game.validMoves(startPos);
        if (!validMoves.contains(move)) {
            throw new DataAccessException("not a valid move.");
        }
    }

    private ChessGame updateGame(ChessMove move, GameData gameData) throws InvalidMoveException {
        ChessGame game = gameData.game();
        game.makeMove(move);
        return game;
    }

    private String formatMove(ChessMove move) {
        Character[] cols = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece.PieceType promoPiece = move.getPromotionPiece();

        var str = new StringBuilder();
        str.append(cols[startPos.getColumn()-1]);
        str.append(startPos.getRow());
        str.append("->");
        str.append(cols[endPos.getColumn()-1]);
        str.append(endPos.getRow());
        if (promoPiece != null) {
            str.append(':');
            str.append(promoPiece);
        }
        return str.toString();
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
