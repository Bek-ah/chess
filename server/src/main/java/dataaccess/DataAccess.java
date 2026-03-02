package dataaccess;

import model.*;

import server.DataBase;

import java.util.HashMap;

public class DataAccess {
    DataBase myDataBase = new DataBase();
    //CREATE
    public void createUser(User userData){
        myDataBase.createUser(userData);
    }
    void createGame(String gn, Integer gID){
        myDataBase.createGame(gID, gn);
    }
    public void createAuth(String username, String authToken){
        myDataBase.createAuth(authToken, new Auth(username, authToken));
    }
    //GET 1
    public User getUserbyUsername(String username){
        return myDataBase.getUserbyUsername(username);
    }
    public Game getGamebyGameID(int id){
        return myDataBase.getGamebyGameID(id);
    }
    public Auth getAuthbyToken(String token){
        //if doesn't exist, throw unauthorized error
        return myDataBase.getAuthbyToken(token);
    }
    //GET ALL
    public HashMap<String, User> getAllUsers(){
        return myDataBase.getUserTable();
    }
    public HashMap<String, Auth> getAllAuth(){
        return myDataBase.getAuthTable();
    }
    public HashMap<Integer, Game> getAllGames(){
        return myDataBase.getGameTable();
    }
    //CLEAR 1
    /*void deleteUser(String username) {
        userTable.remove(username);
    }
    void deleteAuth(String token){
        authTable.remove(token);
    }
    void deleteGame(int gameID) {
        gameTable.remove(gameID);
    }*/
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
