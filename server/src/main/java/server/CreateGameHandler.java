package server;

import dataaccess.DataAccess;
import model.Game;
import service.ChessService;

public class CreateGameHandler {
    public CreateGameHandler(){}
    public Game addGame(String authToken, String gameName, DataAccess dataAccess) throws ClassNotFoundException{
        ChessService service = new ChessService(dataAccess);
        if (!service.authenticate(authToken)){
            throw new IllegalAccessError();
        }
        Game game = service.addGame(authToken,gameName,dataAccess);
        if (game==null){
            throw new ClassNotFoundException();
        }
        return game;
    }
}
