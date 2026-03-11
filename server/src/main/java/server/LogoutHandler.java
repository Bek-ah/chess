package server;

import dataaccess.DataAccess;
import service.ChessService;

public class LogoutHandler {
    public LogoutHandler(){}
    public void logout(String authToken, DataAccess dataAccess) {
        ChessService service = new ChessService(dataAccess);
        service.logout(authToken, dataAccess);
    }
}
