package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import server.requestAndResult.RegisterRequest;
import server.requestAndResult.RegisterResult;
import services.ClearService;
import services.GameService;
import services.UserService;

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
                .delete("/db", this::clearHandler);

        // Register your endpoints and exception handlers here.
        //TODO: exception handlers?

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void registerHandler(Context ctx) {
        //is body going to be a json? or just a string? how do I make it the correct json object?
        //how do I turn it into json to make into a request?
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.result(serializer.toJson(result));
    }

    private void loginHandler(Context ctx) {

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

    }
}
