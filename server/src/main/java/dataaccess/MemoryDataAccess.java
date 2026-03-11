package dataaccess;

import model.*;

import server.DataBase;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    DataBase myDataBase = new DataBase();
    //CREATE
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
    public Game getGamebyGameName(String gameName){
        return myDataBase.getGamebyGameName(gameName);
    }
    public Auth getAuthbyToken(String token){
        return myDataBase.getAuthbyToken(token);
    }
    //GET ALL
    public HashMap<Integer, Game> getAllGames(){
        return myDataBase.getGameTable();
    }
    //CLEAR 1
    public void deleteUser(String username) {
        myDataBase.deleteUser(username);
    }
    public void deleteAuth(String token){
        myDataBase.deleteAuth(token);
    }
    public void deleteGame(int gameID) {
        myDataBase.deleteGame(gameID);
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
