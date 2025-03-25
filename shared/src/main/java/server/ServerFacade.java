package server;

import com.google.gson.Gson;
import model.request.*;
import model.result.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public static class ServerException extends RuntimeException {
        private final int statusCode;

        public ServerException(int statusCode, String message) {
            super("Server returned " + statusCode + ": " + message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    public void clearApplication(){
        String path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public RegisterResult registerUser(RegisterRequest registerRequest){
        String path = "/user";
        return this.makeRequest("POST", path, registerRequest, RegisterResult.class, null);
    }

    public LoginResult loginUser(LoginRequest loginRequest){
        String path = "/session";
        return this.makeRequest("POST", path, loginRequest, LoginResult.class, null);
    }

    public void logoutUser(LogoutRequest logoutRequest, String authToken){
        String path = "/session";
        this.makeRequest("DELETE", path, logoutRequest, LogoutResult.class, authToken);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest, String authToken){
        String path = "/game";
        return this.makeRequest("GET", path, listGamesRequest, ListGamesResult.class, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken){
        String path = "/game";
        return this.makeRequest("POST", path, createGameRequest, CreateGameResult.class, authToken);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken){
        String path = "/game";
        return this.makeRequest("PUT", path, joinGameRequest, JoinGameResult.class, authToken);
    }

    private <T>T makeRequest(String method, String path,Object request, Class<T> responseClass, String token) throws ServerException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (token != null) {
                http.setRequestProperty("Authorization", token);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }catch (Exception e) {
            return null;
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream os = http.getOutputStream()) {
                os.write(reqData.getBytes());
            }catch (Exception e) {}
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) {
        try {
            var status = http.getResponseCode();
            if (status != 200){
                if (status == 401){
                    System.out.println("Unauthorized");
                }else if (status == 403){
                    System.out.println("Already taken");
                }else if (status == 404){
                    System.out.println("Not found");
                }else if (status == 400){
                    System.out.println("Bad request");
                }
            }
        } catch (IOException e) {
            System.out.println("Error Getting response");
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T result = null;
        if (http.getContentLength() < 0){
            try (InputStream readyBody = http.getInputStream()){
                InputStreamReader reader = new InputStreamReader(readyBody);
                if (responseClass != null){
                    result = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return result;
    }
}
