package server;

import exceptions.DataAccessException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import requestandresult.*;
import services.ClearService;
import services.GameService;
import services.UserService;

import java.util.Map;

public class Server {
    Gson serializer = new Gson();
    private final Javalin javalin;

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    private static final MySqlUserDAO USER_DAO;
    private static final MySqlAuthDAO AUTH_DAO;
    private static final MySqlGameDAO GAME_DAO;

    static {
        try {
            USER_DAO = new MySqlUserDAO();
            AUTH_DAO = new MySqlAuthDAO();
            GAME_DAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Server() {
        this(new UserService(USER_DAO, AUTH_DAO),
                new GameService(AUTH_DAO, GAME_DAO),
                new ClearService(USER_DAO, AUTH_DAO, GAME_DAO));
    }

    public Server(UserService userService, GameService gameService, ClearService clearService) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerHandler)
                .post("/session", this::loginHandler)
                .delete("/session", this::logoutHandler)
                .get("/game", this::listGamesHandler)
                .post("/game", this::createGameHandler)
                .put("/game", this::joinGameHandler)
                .delete("/db", this::clearHandler)
                .exception(ForbiddenResponse.class, this::forbiddenHandler)
                .exception(BadRequestResponse.class, this::badReqHandler)
                .exception(UnauthorizedResponse.class, this::unauthorizedHandler)
                .exception(DataAccessException.class, this::generalExHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void registerHandler(Context ctx) throws DataAccessException {
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.header("Authorization", result.authToken());
        ctx.result(serializer.toJson(result));
    }

    private void loginHandler(Context ctx) throws DataAccessException {
        LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        ctx.header("Authorization", result.authToken());
        ctx.result(serializer.toJson(result));
    }

    private void logoutHandler(Context ctx) throws DataAccessException {
        LogoutRequest request = new LogoutRequest(ctx.header("authorization"));
        userService.logout(request);
        ctx.result("");
        ctx.status(200);
    }

    private void listGamesHandler(Context ctx) throws DataAccessException {
        ListGamesRequest request = new ListGamesRequest(ctx.header("authorization"));
        ListGamesResult result = gameService.listGames(request);
        ctx.result(serializer.toJson(result));
    }

    private void createGameHandler(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        JsonObject jsonObject = JsonParser.parseString(ctx.body()).getAsJsonObject();
        JsonElement gameNameObj = jsonObject.get("gameName");
        String gameName;
        if(gameNameObj != null) {
            gameName = gameNameObj.getAsString();
        } else {gameName = null;}
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        CreateGameResult result = gameService.createGame(request);
        ctx.result(serializer.toJson(result));
    }

    private void joinGameHandler(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        JsonObject jsonObject = JsonParser.parseString(ctx.body()).getAsJsonObject();
        JsonElement playerColorObj = jsonObject.get("playerColor");
        JsonElement gameIDObj = jsonObject.get("gameID");
        String playerColor;
        int gameID;
        if(playerColorObj != null) {
            playerColor = playerColorObj.getAsString();
        } else {playerColor = null;}
        if(gameIDObj != null) {
            gameID = gameIDObj.getAsInt();
        } else {gameID = 0;}
        JoinGameRequest request = new JoinGameRequest(authToken, playerColor, gameID);
        gameService.joinGame(request);
    }

    private void clearHandler(Context ctx) throws DataAccessException {
        clearService.clear();
    }

    private void unauthorizedHandler(UnauthorizedResponse e, @NotNull Context context) {
        var body = serializer.toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(401);
        context.json(body);
    }

    private void badReqHandler(BadRequestResponse e, @NotNull Context context) {
        var body = serializer.toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(400);
        context.json(body);
    }

    private void forbiddenHandler(ForbiddenResponse e, @NotNull Context context) {
        var body = serializer.toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(403);
        context.json(body);
    }

    private void generalExHandler(DataAccessException e, Context context) {
        var body = serializer.toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(500);
        context.json(body);
    }
}
