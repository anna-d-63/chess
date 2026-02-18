package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(String gameName) throws DataAccessException {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000);
        while(games.containsKey(id)){
            id = 1000 + random.nextInt();
        }
        GameData g = new GameData(id, "", "", gameName, new ChessGame());
        games.put(id, g);
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        games.remove(gameID);
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(String playerColor, int gameID) throws DataAccessException {

    }
}
