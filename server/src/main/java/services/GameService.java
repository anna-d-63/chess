package services;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;
import server.requestandresult.*;

import java.util.Collection;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request){
        checkNull(request);
        AuthData authData = authorizeUser(request.authToken());
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest request){
        checkNull(request);
        AuthData authData = authorizeUser(request.authToken());
        GameData gameData = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameData.gameID());
    }

    public void joinGame(JoinGameRequest request){
        checkNull(request);
        AuthData authData = authorizeUser(request.authToken());
        GameData game = gameDAO.getGame(request.gameID());
        if (game == null){
            throw new BadRequestResponse("bad request");
        }
        if (request.playerColor().equals("WHITE")){
            String wUsername = game.whiteUsername();
            if (wUsername != null){
                throw new ForbiddenResponse("already taken");
            }
            gameDAO.updateGame("WHITE", authData.username(), game.gameID());
        } else if (request.playerColor().equals("BLACK")) {
            String bUsername = game.blackUsername();
            if (bUsername != null){
                throw new ForbiddenResponse("already taken");
            }
            gameDAO.updateGame("BLACK", authData.username(), game.gameID());
        } else {
            throw new BadRequestResponse("bad request");
        }
    }

    public void checkNull(ParentRequest request) {
        if (request.hasNullFields()){throw new BadRequestResponse("bad request");}
    }

    public AuthData authorizeUser(String authToken){
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new UnauthorizedResponse("unauthorized");
        }
        return authData;
    }
}
