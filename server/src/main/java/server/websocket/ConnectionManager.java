package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    public void broadcast(int gameID, Session theirSession, ServerMessage serverMessage) throws IOException {
        Set<Session> inThisGame = connections.get(gameID);
        for (Session c : inThisGame) {
            if (c.isOpen()) {
                if (serverMessage.getServerMessageType() == NOTIFICATION && !c.equals(theirSession)) {
                    c.getRemote().sendString(serverMessage.getMessage());
                } else if (serverMessage.getServerMessageType() == LOAD_GAME) {
                    c.getRemote().sendString(serverMessage.getMessage());
                } else if (serverMessage.getServerMessageType() == ERROR && c.equals(theirSession)) {
                    c.getRemote().sendString(serverMessage.getMessage());
                }
            }
        }
    }
}
