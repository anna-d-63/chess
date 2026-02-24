package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData createGame(String gameName) { // throws DataAccessException {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000);
        while(games.containsKey(id)){
            id = 1000 + random.nextInt();
        }
        GameData g = new GameData(id, "", "", gameName, new ChessGame());
        games.put(id, g);
        return g;
    }

    @Override
    public void deleteGame(int gameID) { //throws DataAccessException {
        games.remove(gameID);
    }

    @Override
    public GameData getGame(int gameID) { //throws DataAccessException {
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
    public void updateGame(String playerColor, String username, int gameID) { //throws DataAccessException {
        GameData oldGame = games.get(gameID);
        GameData updatedGame;
        if(playerColor == "WHITE"){
            updatedGame = new GameData(gameID, username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        } else {
            updatedGame = new GameData(gameID, oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
        }
        games.put(gameID, updatedGame);
    }
}
