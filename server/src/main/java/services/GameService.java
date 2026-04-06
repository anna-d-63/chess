package services;

import chess.ChessGame;
import dataaccess.AuthDAO;
import exceptions.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;
import requestandresult.*;

import java.util.Collection;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        checkNull(request);
        AuthData authData = authorizeUser(request.authToken());
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        checkNull(request);
        AuthData authData = authorizeUser(request.authToken());
        GameData gameData = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameData.gameID());
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException {
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

    public GameData getGame(String authToken, int gameID) throws DataAccessException {
        authorizeUser(authToken);
        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new UnauthorizedResponse("unauthorized");
        }
        return game;
    }

    public void setGame(int gameID, ChessGame updatedGame) throws DataAccessException {
        gameDAO.setGame(gameID, updatedGame);
    }

    private void checkNull(ParentRequest request) {
        if (request.hasNullFields()){throw new BadRequestResponse("bad request");}
    }

    private AuthData authorizeUser(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null){
            throw new UnauthorizedResponse("unauthorized");
        }
        return authData;
    }
}
