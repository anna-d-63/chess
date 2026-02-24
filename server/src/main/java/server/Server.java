package server;

import io.javalin.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import com.google.gson.Gson;
import server.requestAndResult.RegisterRequest;
import server.requestAndResult.RegisterResult;
import services.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register);

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) {
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = new UserService.register(request);

    }
}
