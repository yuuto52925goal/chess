package client;

import java.util.Arrays;

public class AuthClient {

    private String serverUrl;
    private PregameClient pregameClient;

    public AuthClient(String url) {
        serverUrl = url;
        pregameClient = new PregameClient("");
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
            System.out.println(Arrays.toString(params));
            pregameClient.setAuth("hello123");
            pregameClient.run();
            return String.format("Login %s:%s", params[0], params[1]);
        }
        return "Error";
    }

    public String register (String... params) {
        if (params.length >= 2){
            System.out.println(Arrays.toString(params));
            pregameClient.setAuth("hello1234");
            pregameClient.run();
            return String.format("Register %s:%s", params[0], params[1]);
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
                - Login as an existing user: "l"or "login" <USERNAME> <PASSWORD>
                - Register a  new user: "r"or "register" <USERNAME> <PASSWORD> <EMAIL>
                - EXIST the program: "q", "quit"
                - Print this message: "h", "help"
                """;
    }
}
