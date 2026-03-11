package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.*;
import org.opentest4j.AssertionFailedError;
import service.ChessService;

public class RegisterHandler {
    //final private dataaccess.DataAccess myDataAccess = ;
    public RegisterHandler() throws AssertionFailedError {
    }
    public Auth register(String body, DataAccess dataAccess) throws AssertionFailedError {
        User user = new Gson().fromJson(body, User.class);
        if (user.username()==null || user.password()==null || user.email()==null){
            throw new AssertionFailedError();
        } else {
            ChessService currentService = new ChessService(dataAccess);
            return currentService.regUser(user);
        }
    }
}
