package client;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;

public class AuthClient {

    private String serverUrl;
    private PregameClient pregameClient;
    private ServerFacade serverFacade;

    public AuthClient(String url) {
        serverUrl = url;
        pregameClient = new PregameClient("", url);
        serverFacade = new ServerFacade(serverUrl);
    }

    public String eval (String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var args = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "l" -> login(args);
                case "q" -> quit();
                case "r" -> register(args);
                default -> help();
            };
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String login(String... params) {
        if (params.length >= 2){
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            LoginResult loginResult = serverFacade.loginUser(loginRequest);
            pregameClient.setAuth(loginResult.authToken());
            System.out.println("Successfully logged in");
            pregameClient.run();
            return "Log out";
        }
        return "Error";
    }

    public String register (String... params) {
        if (params.length >= 3){
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult = serverFacade.registerUser(registerRequest);
            pregameClient.setAuth(registerResult.authToken());
            System.out.println("Successfully registered");
            pregameClient.run();
            return "Log out";
        }
        return "Error";
    }

    public String quit (){
        System.out.println("Goodbye!");
        return "quit";
    }


    public String help(){
        return """
                Options:
                - Login as an existing user: "l" <USERNAME> <PASSWORD>
                - Register a  new user: "r" <USERNAME> <PASSWORD> <EMAIL>
                - EXIST the program: "q", "quit"
                - Print this message: "h", "help"
                """;
    }
}
