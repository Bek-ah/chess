package service;

import dataaccess.DataAccess;
import org.opentest4j.AssertionFailedError;
import passoff.exception.ResponseParseException;
import model.*;

import java.rmi.AlreadyBoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    public Auth regUser(User userData) throws AssertionFailedError {
        if(getUser(userData) == null){
            createUser(userData);
            return createAuth(userData.username());
        } else {
            throw new AssertionFailedError();
        }
    }
    public User getUser(User userData) throws AssertionError {
        User returnData = dataAccess.getUserbyUsername(userData.username());
        if (returnData==null){
            return null;
        } else {
            throw new AssertionError();
        }
    }
    public boolean authenticate(String authToken){
        if (dataAccess.getAuthbyToken(authToken)!=null){
            return true;
        };
        return false;
    }
    public void createUser(User userData){
        dataAccess.createUser(userData);
    }
    public Auth createAuth(String username){
        String authToken = UUID.randomUUID().toString();
        Auth newAuth = new Auth(username, authToken);
        dataAccess.createAuth(newAuth);
        return newAuth;
    }
    public void deleteAllGames() throws ResponseParseException{
        dataAccess.deleteAllAuth();
        dataAccess.deleteAllUsers();
        dataAccess.deleteAllGames();
    }
    public Collection<Game> getAllMyGames(){
        HashMap<Integer,Game> gameHash = dataAccess.getAllGames();
        return gameHash.values();
    }
}
