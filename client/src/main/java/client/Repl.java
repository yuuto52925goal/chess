package client;
import client.AuthClient;

import java.util.Scanner;

public class Repl {

    private final AuthClient authClient;

    public Repl(String serverUrl) {
        this.authClient = new AuthClient(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to chess. Sign in to start");
        System.out.print(authClient.help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            String line = scanner.nextLine();
            try{
                result = authClient.eval(line);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
