package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String message;
    ChessGame.TeamColor color;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message, ChessGame.TeamColor color) {
        this.serverMessageType = type;
        this.message = message;
        this.color = color;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage(){
        return message;
    }

    public ChessGame.TeamColor getColor(){
        return null;
    }

    public void setColor(ChessGame.TeamColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    @Override
    public String toString() {
        return "ServerMessage{" +
                "serverMessageType=" + serverMessageType +
                ", message='" + message + '\'' +
                ", color=" + color +
                '}';
    }
}
