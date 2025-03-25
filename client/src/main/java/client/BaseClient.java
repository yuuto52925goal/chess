package client;

import java.util.Scanner;

public abstract class BaseClient {
    protected Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println(help());
        drawBoard();
        var result = "";
        while (!shouldExit(result)) {
            String input = scanner.nextLine();
            try {
                result = this.eval(input);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    protected abstract void drawBoard();
    protected abstract boolean shouldExit(String result);
    protected abstract String eval(String input);
    protected abstract String help();
}