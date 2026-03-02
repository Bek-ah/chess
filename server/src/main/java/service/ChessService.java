package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import passoff.exception.ResponseParseException;
import model.*;

import java.nio.channels.AlreadyBoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    public Auth regUser(User userData){
        if(getUser(userData) == null){
            createUser(userData);
            return createAuth(userData.userName());
        } else {
            return null;
        }
    }
    public User getUser(User userData) {
        User returnData = dataAccess.getUserbyUsername(userData.userName());
        if (returnData==null){
            return null;
        } else {
            return userData;
        }
    }
    public void createUser(User userData){
        dataAccess.createUser(userData);
    }
    public Auth createAuth(String username){
        String authToken = UUID.randomUUID().toString();
        dataAccess.createAuth(username, authToken);
        return dataAccess.getAuthbyToken(authToken);
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
