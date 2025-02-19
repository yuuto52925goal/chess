package server;

import handler.RegisterHandler;
import service.UserService;
import spark.*;

public class Server {

    private final UserService userService;

    public Server(UserService userService) {
        this.userService = userService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("main/resources/web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> new RegisterHandler(userService).handleRegister(req, res));
//        Spark.post("/user", (req, res) -> userService.)
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
