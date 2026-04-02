package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {
    public ChessGame.TeamColor color;

    public LoadGameMessage(ChessGame game, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME, new Gson().toJson(game));
        this.color = color;
    }

    @Override
    public ChessGame.TeamColor getColor(){
        return color;
    }
}
