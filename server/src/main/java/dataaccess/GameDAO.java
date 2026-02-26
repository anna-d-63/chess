package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName);
    void clearGames();
    Collection<GameData> listGames();
    GameData getGame(int gameID);
    void updateGame(String playerColor, String username, int gameID);
}
