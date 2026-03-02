package server;

import dataaccess.DataAccess;
import service.ChessService;

public class ClearHandler(DataAccess dataAccess) {
    ChessService service = new ChessService(dataAccess);
    public void deleteAll(){
        service.deleteAllGames();
    }
}
