package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {
    public ChessGame game;
    public ChessGame.TeamColor color;


    public LoadGameMessage(ServerMessageType type, ChessGame game, ChessGame.TeamColor color) {
        super(type);
        this.game = game;
        this.color = color;
    }

    @Override
    public String getMessage() {
        return new Gson().toJson(game);
    }
}
