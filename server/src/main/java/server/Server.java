package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.Javalin;
import io.javalin.http.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import server.requestAndResult.*;
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
    private static final MemoryUserDAO userDAO = new MemoryUserDAO();
    private static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private static final MemoryGameDAO gameDAO = new MemoryGameDAO();

    public Server(){
        this(new UserService(userDAO, authDAO),
                new GameService(authDAO, gameDAO),
                new ClearService(userDAO, authDAO, gameDAO));
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
                .exception(UnauthorizedResponse.class, this::unauthorizedHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void registerHandler(Context ctx) {
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.header("Authorization", result.authToken());
        ctx.result(serializer.toJson(result));
    }

    private void loginHandler(Context ctx) {
        LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        ctx.header("Authorization", result.authToken());
        ctx.result(serializer.toJson(result));
    }

    //TODO: a server error is occurring for some reason
    private void logoutHandler(Context ctx) {
        LogoutRequest request = serializer.fromJson(ctx.header("authorization"), LogoutRequest.class);
        userService.logout(request);
        ctx.result("");
        ctx.status(200);
    }

    private void listGamesHandler(Context ctx) {
        ListGamesRequest request = serializer.fromJson(ctx.header("authorization"), ListGamesRequest.class);
        ListGamesResult result = gameService.listGames(request);
        ctx.result(serializer.toJson(result));
    }

    private void createGameHandler(Context ctx) {
        String authToken = ctx.header("authorization");
        JsonObject jsonObject = JsonParser.parseString(ctx.body()).getAsJsonObject();
        String gameName = jsonObject.get("gameName").getAsString();
        CreateGameRequest request = new CreateGameRequest(authToken, gameName);
        CreateGameResult result = gameService.createGame(request);
        ctx.result(serializer.toJson(result));
    }

    private void joinGameHandler(Context ctx) {
        String authToken = ctx.header("authorization");
        JsonObject jsonObject = JsonParser.parseString(ctx.body()).getAsJsonObject();
        String playerColor = jsonObject.get("playerColor").getAsString();
        int gameID = jsonObject.get("gameID").getAsInt();
        JoinGameRequest request = new JoinGameRequest(authToken, playerColor, gameID);
        gameService.joinGame(request);
    }

    private void clearHandler(Context ctx) {
        try {
            clearService.clear();
            ctx.status(200);
        } catch (Exception e) {
            ctx.status(500);
            throw new InternalServerErrorResponse();
        }
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
}
