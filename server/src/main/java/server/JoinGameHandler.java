package server;

import com.google.gson.Gson;
import com.mysql.cj.exceptions.WrongArgumentException;
import dataaccess.DataAccess;
import model.User;
import service.ChessService;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessGame.TeamColor.BLACK;

public class JoinGameHandler {
    public JoinGameHandler(){
    }
    public void joinGame(String authToken, String myBody, DataAccess dataAccess) throws WrongArgumentException {
        JoinGameInput body = new Gson().fromJson(myBody, JoinGameInput.class);
        ChessService service = new ChessService(dataAccess);
        //CHECK IF PLAYER COLOR IS CORRECT
        if(body.playerColor().equals(WHITE) || body.playerColor().equals(BLACK)) {
            service.joinGame(authToken, body.playerColor(), body.gameID(), dataAccess);
        } else {
            throw new WrongArgumentException();
        }
    }
}
