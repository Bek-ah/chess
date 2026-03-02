package server;

import dataaccess.DataAccess;
import model.*;
import service.ChessService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.rmi.AlreadyBoundException;
import java.util.Map;

public class RegisterHandler {
    final private DataAccess myDataAccess = new DataAccess();
    public RegisterHandler(User user) throws AlreadyBoundException {
    }
    public Context register(User user, Context ctx) throws AlreadyBoundException {
        ChessService currentService = new ChessService(myDataAccess);
        currentService.getUser(user.userName());
        String newUser = currentService.createUser(user);
        String newAuth = currentService.createAuth(user.userName()).authtoken();
        return ctx.json(Map.of(newUser,newAuth));
    }
}
