package client.ui;

import chess.ChessGame;
import model.GameData;

public interface ClientUI {
    String eval(String line);
    String firstLine();
    String help();
    String getAuthToken();
    GameData getGameData();
    ChessGame.TeamColor getColor();
}
