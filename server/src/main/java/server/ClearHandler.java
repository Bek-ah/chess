package server;

import dataaccess.DataAccess;
import service.ChessService;

public class ClearHandler {
    final private DataAccess myDataAccess = new DataAccess();
    public ClearHandler(){
        ChessService service = new ChessService(myDataAccess);
        service.deleteAllGames();
    }
}
