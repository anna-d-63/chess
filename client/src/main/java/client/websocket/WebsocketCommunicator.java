package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class WebsocketCommunicator extends Endpoint {

    //SENDS AND RECEIVES WEBSOCKET COMMUNICATIONS

    Session session;
    ServerMessageObserver observer;
    Gson serializer = new Gson();

    public WebsocketCommunicator (String url, ServerMessageObserver observer) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });

        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectToGame(String authToken, int gameID, ChessGame.TeamColor color) throws DataAccessException {
        try {
            var command = new UserGameCommand(CONNECT, authToken, gameID, color);
            this.session.getBasicRemote().sendText(serializer.toJson(command));
        } catch (IOException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void makeAMove(
            String authToken, int gameID, ChessGame.TeamColor color, ChessMove move) throws DataAccessException {}

    public void leaveGame(String authToken, int gameID, ChessGame.TeamColor color) throws DataAccessException {}

    public void resignFromGame(String authToken, int gameID, ChessGame.TeamColor color) throws DataAccessException {}

    private void handleMessage(String messageString) {
        try {
            ServerMessage message = serializer.fromJson(messageString, ServerMessage.class);
            observer.notify(message);
        } catch (Exception e) {
            observer.notify(new ErrorMessage(ERROR, e.getMessage()));
        }
    }
}
