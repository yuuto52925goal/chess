package server;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    private <T>T makeRequest(String method, String path,Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream os = http.getOutputStream()) {
                os.write(reqData.getBytes());
            }catch (Exception e) {
                System.out.println("Error writing request body");
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) {
        try {
            var status = http.getResponseCode();
            if (status != 200){
                try (InputStream respErr = http.getErrorStream()) {
                    if (respErr != null) {
                        System.out.println("Error writing response body");
                    }
                } catch (IOException e) {
                    System.out.println("Error writing response body");
                }
            }
        } catch (IOException e) {
            System.out.println("Error Getting response body");
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) {
        T result = null;
        if (http.getContentLength() < 0){
            try (InputStream readyBody = http.getInputStream()){
                InputStreamReader reader = new InputStreamReader(readyBody);
                if (responseClass != null){
                    result = new Gson().fromJson(reader, responseClass);
                }
            }catch (Exception e){
                System.out.println("Error reading response body");
            }
        }
        return result;
    }
}
