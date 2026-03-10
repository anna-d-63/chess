
import dataaccess.*;
import server.Server;
import services.ClearService;
import services.GameService;
import services.UserService;

public class ServerMain {
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1){
                port = Integer.parseInt(args[0]);
            }

            UserDAO userDAO = new MySqlUserDAO();
            AuthDAO authDAO = new MySqlAuthDAO();
            GameDAO gameDAO = new MySqlGameDAO();

            var userService = new UserService(userDAO, authDAO);
            var gameService = new GameService(authDAO, gameDAO);
            var clearService = new ClearService(userDAO, authDAO, gameDAO);

            Server server = new Server(userService, gameService, clearService);
            server.run(port);
        } catch(Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("♕ 240 Chess Server");
    }
}