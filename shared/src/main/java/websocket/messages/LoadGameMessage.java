package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {

    public LoadGameMessage(ChessGame game, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME, new Gson().toJson(game), color);
    }

    @Override
    public ChessGame.TeamColor getColor(){
        return color;
    }
}
