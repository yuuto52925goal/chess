package client;
import client.AuthClient;

public class Repl {

    private final AuthClient authClient;

    public Repl(String serverUrl) {
        this.authClient = new AuthClient();
    }

    public void run(){
        System.out.println("Welcome to chess. Sign in to start");

    }
}
