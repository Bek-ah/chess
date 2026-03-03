package server;

import dataaccess.DataAccess;
import model.Auth;
import model.Game;
import service.ChessService;

import java.nio.file.AccessDeniedException;
import java.util.Collection;

public class ListGamesHandler {
    public ListGamesHandler(){}
    public Collection<Game> getGameList(String authToken, DataAccess dataAccess) throws AccessDeniedException {
        ChessService currentService = new ChessService(dataAccess);
        currentService.getAllMyGames(authToken);
        Collection<Game> gameList = currentService.getAllMyGames(authToken);
        System.out.println("gameListHandler: "+gameList);
        return gameList;
    }
}
