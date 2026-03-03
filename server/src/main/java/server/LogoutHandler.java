package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.Auth;
import model.User;
import service.ChessService;

import java.nio.file.AccessDeniedException;
import java.util.MissingFormatArgumentException;
import java.util.NoSuchElementException;

public class LogoutHandler {
    public LogoutHandler(){}
    public void logout(String authToken, DataAccess dataAccess) {
        ChessService service = new ChessService(dataAccess);
        service.logout(authToken, dataAccess);
    }
}
