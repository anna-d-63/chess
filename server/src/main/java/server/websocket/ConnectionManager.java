package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static websocket.commands.UserGameCommand.CommandType.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class ConnectionManager {
    public final HashMap<Integer, Set<Session>> connections = new HashMap<>();

    public void saveSession(int gameID, Session session){
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new HashSet<>());
        }
        connections.get(gameID).add(session);
    }

    public void broadcast(UserGameCommand command, Session theirSession, String serverMessage,
                          ServerMessage.ServerMessageType type) throws IOException {
        int gameID = command.getGameID();
        UserGameCommand.CommandType commandType = command.getCommandType();

        Set<Session> inThisGame = connections.get(gameID);
        for (Session c : inThisGame) {
            if (c.isOpen()) {
                if (type == NOTIFICATION) {
                    if (!c.equals(theirSession)) {
                        c.getRemote().sendString(serverMessage);
                    } else if (( commandType == RESIGN || statusMessage(serverMessage)) && c.equals(theirSession)) {
                        c.getRemote().sendString(serverMessage);
                    }
                } else if (type == LOAD_GAME) {
                    if ((commandType == CONNECT && c.equals(theirSession)) || commandType == MAKE_MOVE) {
                        c.getRemote().sendString(serverMessage);
                    }
                } else if (type == ERROR && c.equals(theirSession)) {
                    c.getRemote().sendString(serverMessage);
                }
            }
        }
        if (commandType == LEAVE) {
            connections.get(gameID).remove(theirSession);
        }
    }

    private boolean statusMessage(String serverMessage) {
        NotificationMessage notification = new Gson().fromJson(serverMessage, NotificationMessage.class);
        String message = notification.getMessage();
        return message.contains("check") ||
                message.contains("checkmate") ||
                message.contains("stalemate");
    }
}
