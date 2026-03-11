package server;

import com.google.gson.Gson;
import com.mysql.cj.exceptions.WrongArgumentException;
import dataaccess.DataAccess;
import service.ChessService;

import java.rmi.AlreadyBoundException;


public class JoinGameHandler {
    public JoinGameHandler() {
    }
    public void joinGame(String authToken, String myBody, DataAccess dataAccess)
            throws WrongArgumentException, AlreadyBoundException, AssertionError {
        JoinGameInput body = new Gson().fromJson(myBody, JoinGameInput.class);
        ChessService service = new ChessService(dataAccess);
        //CHECK IF INPUT IS CORRECT
        if(body.playerColor()==null){
            throw new WrongArgumentException();
        }
        if(body.playerColor().equals("WHITE") || body.playerColor().equals("BLACK")) {
            if(body.gameID()!= null && authToken != null) {
                service.joinGame(authToken, body.playerColor(), body.gameID(), dataAccess);
            } else {
                throw new WrongArgumentException();
            }
        } else {
            throw new WrongArgumentException();
        }
    }
}
