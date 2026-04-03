package websocket.messages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {

    String game;
    ChessGame.TeamColor color;

    public LoadGameMessage(String game, ChessGame.TeamColor color) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.color = color;
    }

    public String getGame(){
        return game;
    }

    public ChessGame.TeamColor getColor(){
        return color;
    }
}
