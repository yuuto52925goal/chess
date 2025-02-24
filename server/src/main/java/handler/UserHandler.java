package handler;

import com.google.gson.Gson;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.ErrorResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handleLogin(Request request, Response response) {
        try{
            LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
            Object result = userService.login(loginRequest);

            if (result instanceof ErrorResponse) {
                response.status(401);
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }else{
                response.status(200);
                return new Gson().toJson(result);
            }
        }catch (Exception e){
            response.status(500);
            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object handleRegister(Request request, Response response) {
        try{
            RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
            Object result = userService.register(registerRequest);
            if (result instanceof ErrorResponse) {
                if (((ErrorResponse) result).message().equals("Error: already taken")) {
                    response.status(403);
                    return new Gson().toJson(new ErrorResponse("Error: already taken"));
                }else{
                    response.status(400);
                    return new Gson().toJson(new ErrorResponse("Error: bad request"));
                }
            }else{
                response.status(200);
                return new Gson().toJson(result);
            }
        }catch(Exception e){
            response.status(500);
            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object handleLogout(Request request, Response response) {
        try{
            LogoutRequest logoutRequest = new Gson().fromJson(request.body(), LogoutRequest.class);
            Object result = userService.logout(logoutRequest);
            if (result instanceof ErrorResponse) {
                response.status(401);
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }else{
                response.status(200);
                return new Gson().toJson(result);
            }
        }catch (Exception e){
            response.status(500);
            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public String authCheck(String token){
        return userService.authCheck(token);
    }

//    public void clear
}
