package client.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import model.Game;
import ui.DrawBoard;
import websocket.messages.ServerMessage;

public class LoadGameMessage implements NotificationHandler {

    @Override
    public void notify(ServerMessage notification) {
        ChessGame game = notification.getGame();//draw board?

    }

    @Override
    public void loadGame(ChessGame game){
        boolean isBlack = game.getTeamTurn() == ChessGame.TeamColor.BLACK;
        new DrawBoard(isBlack,game, new ChessPosition(0,0));
        System.out.println();
        System.out.print("GAME>> ");
    }
}
