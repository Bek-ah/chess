package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import passoff.exception.ResponseParseException;
import model.*;

import java.rmi.AlreadyBoundException;
import java.util.Collection;

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
    public void deleteAllGames() throws ResponseParseException{
        dataAccess.deleteAllAuth();
        dataAccess.deleteAllUsers();
        dataAccess.deleteAllGames();
    }
}
