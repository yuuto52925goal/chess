package server;

import handler.UserHandler;
import service.UserService;
import spark.*;

public class Server {

    private final UserService userService;
    private final UserHandler userHandler;

    public Server() {
        this.userService = new UserService();
        userHandler = new UserHandler(userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("main/resources/web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", userHandler::handleRegister);
        Spark.post("/session", userHandler::handleLogin);
        Spark.post("/logout", userHandler::handleLogout);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
