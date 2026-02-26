package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData createGame(String gameName) {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000);
        while(games.containsKey(id)){
            id = 1000 + random.nextInt();
        }
        GameData g = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, g);
        return g;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void clearGames() { //throws DataAccessException {
        games.clear();
    }

    @Override
    public Collection<GameData> listGames() { //throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(String playerColor, String username, int gameID) {
        GameData oldGame = games.get(gameID);
        GameData updatedGame;
        if(playerColor.equals("WHITE")){
            updatedGame = new GameData(gameID, username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        } else {
            updatedGame = new GameData(gameID, oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
        }
        games.put(gameID, updatedGame);
    }


    @Override
    public String toString() {
        return "MemoryGameDAO{" +
                "games=" + games +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryGameDAO that = (MemoryGameDAO) o;
        return Objects.equals(games, that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(games);
    }


    public HashMap<Integer, GameData> getGames() {
        return games;
    }
}
