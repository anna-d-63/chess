package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.Javalin;
import io.javalin.http.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import server.requestAndResult.LoginRequest;
import server.requestAndResult.LoginResult;
import server.requestAndResult.RegisterRequest;
import server.requestAndResult.RegisterResult;
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

    public Server(){
        this(new UserService(new MemoryUserDAO(), new MemoryAuthDAO()),
                new GameService(new MemoryAuthDAO(), new MemoryGameDAO()),
                new ClearService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO()));
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
        ctx.result(serializer.toJson(result));
    }

    private void loginHandler(Context ctx) {
        LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        ctx.result(serializer.toJson(result));
    }

    private void logoutHandler(Context ctx) {

    }

    private void listGamesHandler(Context ctx) {

    }

    private void createGameHandler(Context ctx) {

    }

    private void joinGameHandler(Context ctx) {

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
        var body = serializer.toJson(Map.of("Error", e.getMessage()));
        context.status(400);
        context.json(body);
    }

    private void badReqHandler(BadRequestResponse e, @NotNull Context context) {
        var body = serializer.toJson(Map.of("Error", e.getMessage()));
        context.status(400);
        context.json(body);
    }

    private void forbiddenHandler(ForbiddenResponse e, @NotNull Context context) {
        var body = serializer.toJson(Map.of("Error", e.getMessage()));
        context.status(403);
        context.json(body);
    }
}
