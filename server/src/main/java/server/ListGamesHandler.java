package server;

import javax.naming.Context;

import dataaccess.DataAccess;
import model.Auth;
import model.Game;
import service.ChessService;

import java.util.Collection;

public class ListGamesHandler {
    final private DataAccess myDataAccess = new DataAccess();
    Collection<Game> gameList;
    public ListGamesHandler(Auth auth){
        ChessService currentService = new ChessService(myDataAccess);
        String authToken;
        Collection<Game> gameList = currentService.getAllMyGames();
    }

    public Collection<Game> getGameList() {
        return gameList;
    }
}
