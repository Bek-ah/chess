package service;

import dataaccess.DataAccess;
import org.opentest4j.AssertionFailedError;
import passoff.exception.ResponseParseException;
import model.*;

import javax.xml.crypto.Data;
import java.rmi.AlreadyBoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    public void joinGame(String authToken, String playerColor, int gameID, DataAccess dataAccess){
        if(dataAccess.getAuthbyToken(authToken)==null){
            int i = 0;//throw error
        }
        Game game = dataAccess.getGamebyGameID(gameID);
        if(game==null){
            int i = 0;//throw error
        }
        if(playerColor.equals(BLACK)){
            game.getBlackUsername();//add already taken exception
        } else if (playerColor.equals(WHITE)){
            game.getWhiteUsername();
        }
        //addplayer
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
    public void logout(String authToken, DataAccess dataAccess) throws NoSuchElementException {
        if(!authenticate(authToken)){
            throw new NoSuchElementException();
        }
        dataAccess.deleteAuth(authToken);
    }
    public Game addGame(String authToken, String gameName, DataAccess dataAccess){
        if(!authenticate(authToken)){
            return null;
        }
        Game gameData = dataAccess.getGamebyGameName(gameName);
        if (gameData!=null){
            return null;
        }
        UUID uuid = UUID.randomUUID();
        int id = uuid.hashCode();
        if (id < 0){
            id *= -1;
        }
        return dataAccess.createGame(gameName, id);
    }
    public boolean authenticate(String authToken){ //true means authToken exists
        if (dataAccess.getAuthbyToken(authToken)==null){
            return false;
        };
        return true;
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
