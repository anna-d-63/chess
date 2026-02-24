package services;

import model.AuthData;
import model.GameData;
import server.requestAndResult.*;

import java.util.Collection;

public class GameService {
    public ListGamesResult listGames(ListGamesRequest request){
        AuthData authData = authorizeUser(request.authToken());
        Collection<GameData> games = db.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest request){
        AuthData authData = authorizeUser(request.authToken());
        GameData gameData = db.createGame(request.gameName());
        return new CreateGameResult(gameData.gameID());
    }

    public void joinGame(JoinGameRequest request){
        AuthData authData = authorizeUser(request.authToken());
        GameData game = db.getGame(request.gameID());
        if (game == null){
            //400 bad request
            System.out.println("Bad Request")
        }
        if (request.playerColor() == "WHITE"){
            String wUsername = game.whiteUsername();
            if (wUsername != null){
                //403 Already Taken Exception
                System.out.println("Already Taken");
            }
            db.updateGame("WHITE", authData.username(), game.gameID());
        } else {
            String bUsername = game.blackUsername();
            if (bUsername != null){
                //403 Already Taken Exception
                System.out.println("Already Taken");
            }
            db.updateGame("BLACK", authData.username(), game.gameID());
        }
    }

    public AuthData authorizeUser(String authToken){
        AuthData authData = db.getAuth(authToken);
        if (authData == null){
            //401 unauthorized exception
            System.out.println("Unauthorized");
        }
        return authData;
    }
}
