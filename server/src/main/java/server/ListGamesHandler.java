package server;

import dataaccess.DataAccess;
import model.Auth;
import model.Game;
import service.ChessService;

import java.util.Collection;

public class ListGamesHandler {
    final private DataAccess myDataAccess;
    Collection<Game> gameList;
    String authToken;
    public ListGamesHandler(Auth auth, DataAccess tempDataAccess){
        myDataAccess = tempDataAccess;
        ChessService currentService = new ChessService(myDataAccess);
        authToken = auth.authToken();
        authenticate();
        Collection<Game> gameList = currentService.getAllMyGames();
    }
    public void authenticate(){
        System.out.println(authToken);
        myDataAccess.getAuthbyToken(authToken);//if false, throw unauthorized exception
    }

    public Collection<Game> getGameList() {
        System.out.println(gameList);
        return gameList;
    }
}
