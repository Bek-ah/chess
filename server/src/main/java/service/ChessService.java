package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import passoff.exception.ResponseParseException;
import model.*;

import java.rmi.AlreadyBoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    public User getUser(String username) throws AlreadyBoundException {
        User returnData = dataAccess.getUserbyUsername(username);
        if (dataAccess.getUserbyUsername(username)==null){
            dataAccess.createUser(returnData);
            return returnData;
        } else {
            throw new AlreadyBoundException();
        }
    }
    public String createUser(User userData){
        dataAccess.createUser(userData);
        return userData.userName();
    }
    public Auth createAuth(String username){//TODO: change to param type Auth
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
