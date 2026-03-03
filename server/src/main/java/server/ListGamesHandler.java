package server;

import dataaccess.DataAccess;
import model.Auth;
import model.Game;
import service.ChessService;

import java.util.Collection;

public class ListGamesHandler {
    public ListGamesHandler(){}

    public Collection<Game> getGameList(String authToken, DataAccess dataAccess) {
        ChessService currentService = new ChessService(dataAccess);
        currentService.authenticate(authToken);
        Collection<Game> gameList = currentService.getAllMyGames();
        System.out.println("gameListHandler: "+gameList);
        return gameList;
    }
}
