package server;

import handler.RegisterHandler;
import service.UserService;
import spark.*;

public class Server {

    private final UserService userService;
    private final RegisterHandler registerHandler;

    public Server() {
        this.userService = new UserService();
        this.registerHandler = new RegisterHandler(userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("main/resources/web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", registerHandler::handleRegister);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
