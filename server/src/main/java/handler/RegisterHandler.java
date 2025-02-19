package handler;

import model.request.RegisterRequest;
import model.result.ErrorResponse;
import service.UserService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService userService){
        this.userService = userService;
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
}
