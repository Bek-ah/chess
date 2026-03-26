package server;

import model.Auth;
import model.Game;
import model.User;

import java.util.HashMap;

public class DataBase {
    final private HashMap<String, User> userTable = new HashMap<>();
    final private HashMap<String, Auth> authTable = new HashMap<>();
    final private HashMap<Integer, Game> gameTable = new HashMap<>();

    public DataBase(){}
    public HashMap<Integer, Game> getGameTable(){
        return gameTable;
    }
    public void deleteAllUsers() {
        userTable.clear();
    }
    public void deleteAllAuth(){
        authTable.clear();
    }
    public void deleteAllGames() {
        gameTable.clear();
    }
    public void deleteAuth(String token){
        authTable.remove(token);
    }
    public User getUserbyUsername(String username){
        if (userTable.get(username)!=null){
            return userTable.get(username);
        }
        return null;
    }
    public Game getGamebyGameID(int id){
        return gameTable.get(id);
    }
    public Auth getAuthbyToken(String token){
        return authTable.get(token);
    }
    public void createUser(User userData){
        userTable.put(userData.username(), userData);
    }
    public Game createGame(int gID, String gn){
        Game newGame = new Game(gID, gn);
        gameTable.put(gID, new Game(gID, gn));
        return newGame;
    }
    public void createAuth(String authToken, Auth authData){
        authTable.put(authToken, new Auth(authData.username(),authData.authToken()));
    }
}
