package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.Auth;
import model.User;
import service.ChessService;

import java.nio.file.AccessDeniedException;
import java.util.MissingFormatArgumentException;
import java.util.NoSuchElementException;

public class LogoutHandler {
    public LogoutHandler(){}
    public Auth logout(String authToken, DataAccess dataAccess) throws NoSuchElementException, AccessDeniedException, MissingFormatArgumentException {
        ChessService service = new ChessService(dataAccess);
        if(!service.authenticate(authToken)){
            throw new NoSuchElementException();
        };
        User foundUser = dataAccess.getUserbyUsername(user.username());
        if(foundUser==null){
            throw new NoSuchElementException("User not found"); //username doesn't exist
        } else if (!foundUser.password().equals(user.password())){
            throw new AccessDeniedException("Username or password is incorrect");
        } else {
            return service.createAuth(foundUser.username());
        }
    }
}
