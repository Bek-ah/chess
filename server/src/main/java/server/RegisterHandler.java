package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.*;
import service.ChessService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.rmi.AlreadyBoundException;
import java.util.Map;

public class RegisterHandler {
    final private DataAccess myDataAccess = new DataAccess();
    public RegisterHandler() throws AlreadyBoundException {
    }
    public Auth register(String body) throws AlreadyBoundException {
        User user = new Gson().fromJson(body, User.class);
        ChessService currentService = new ChessService(myDataAccess);
        currentService.regUser(user);//TODO: move this logic to be inside createUser logic in ChessService
        return currentService.createAuth(user.userName());
    }
}
