package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;
import static websocket.messages.ServerMessage.ServerMessageType.*;

public class ConnectionManager {
    public final HashMap<Integer, Set<Session>> connections = new HashMap<>();

    public void saveSession(int gameID, Session session) {}

    public void add(int gameID, Session session){
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new HashSet<>());
        }
        connections.get(gameID).add(session);
    }

    public void broadcast(UserGameCommand command, Session theirSession, ServerMessage serverMessage) throws IOException {
        int gameID;
        UserGameCommand.CommandType type;
        if (command == null) {
            gameID = -1;
            type = CONNECT;
        } else {
            gameID = command.getGameID();
            type = command.getCommandType();
        }
        Set<Session> inThisGame = connections.get(gameID);
        String json = new Gson().toJson(serverMessage);
        for (Session c : inThisGame) {
            if (c.isOpen()) {
                if (serverMessage.getServerMessageType() == NOTIFICATION && !c.equals(theirSession)) {
                    c.getRemote().sendString(json);
                } else if (serverMessage.getServerMessageType() == LOAD_GAME) {
                    if ((type == CONNECT && c.equals(theirSession)) || type == MAKE_MOVE) {
                        c.getRemote().sendString(json);
                    }
                } else if (serverMessage.getServerMessageType() == ERROR && c.equals(theirSession)) {
                    c.getRemote().sendString(json);
                }
            }
        }
    }
}
