package dataaccess;

import model.Auth;
import model.Game;
import model.User;
import server.DataBase;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    DataBase myDataBase = new DataBase();
    //CREATE
    public void updatePlayers(String whiteUsername, String blackUsername, Integer gameID){}
    public void updatePiece(String start, String end, Integer gameID){}
    public void createUser(User userData){
        myDataBase.createUser(userData);
    }
    public Game createGame(String gn, int gID){
        return myDataBase.createGame(gID, gn);
    }
    public void createAuth(Auth newAuth){
        myDataBase.createAuth(newAuth.authToken(), newAuth);
    }
    //GET 1
    public User getUserbyUsername(String username){
        return myDataBase.getUserbyUsername(username);
    }
    public Game getGamebyGameID(int id){
        return myDataBase.getGamebyGameID(id);
    }

    public Auth getAuthbyToken(String token){
        return myDataBase.getAuthbyToken(token);
    }
    //GET ALL
    public HashMap<Integer, Game> getAllGames(){
        return myDataBase.getGameTable();
    }

    public void deleteAuth(String token){
        myDataBase.deleteAuth(token);
    }

    //CLEAR ALL
    public void deleteAllUsers() {
        myDataBase.deleteAllUsers();
    }
    public void deleteAllAuth(){
        myDataBase.deleteAllAuth();
    }
    public void deleteAllGames() {
        myDataBase.deleteAllGames();
    }
}
