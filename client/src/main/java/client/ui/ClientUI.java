package client.ui;

import chess.ChessGame;
import exceptions.DataAccessException;
import model.GameData;

public interface ClientUI {
    String eval(String line);
    String firstLine() throws DataAccessException;
    String help();
    String getAuthToken();
    GameData getGameData();
    ChessGame.TeamColor getColor();
}
