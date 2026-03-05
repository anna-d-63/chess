package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO {

    @Override
    public GameData createGame(String gameName) {
        return null;
    }

    @Override
    public void clearGames() {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(String playerColor, String username, int gameID) {

    }

    //get games

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
