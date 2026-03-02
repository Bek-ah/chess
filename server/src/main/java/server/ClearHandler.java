package server;

import dataaccess.DataAccess;
import service.ChessService;

public class ClearHandler {
    public ClearHandler(DataAccess dataAccess){
        ChessService service = new ChessService(dataAccess);
        service.deleteAllGames();
    }
}
