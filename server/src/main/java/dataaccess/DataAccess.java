package dataaccess;

import model.Auth;
import model.Game;
import model.User;

import java.util.HashMap;

public interface DataAccess {
    //CREATE
    public void updatePlayers(String whiteUsername, String blackUsername, Integer gameID);
    public void updatePiece(String start, String end, Integer gameID);

    public void createUser(User userData);

    public Game createGame(String gn, int gID);

    public void createAuth(Auth newAuth);

    //GET 1
    public User getUserbyUsername(String username);

    public Game getGamebyGameID(int id);

    public Auth getAuthbyToken(String token);

    //GET ALL
    public HashMap<Integer, Game> getAllGames();

    public void deleteAuth(String token);

    //CLEAR ALL
    public void deleteAllUsers();
    public void deleteAllAuth();
    public void deleteAllGames();
}
