package client;

public class AuthClient {

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
