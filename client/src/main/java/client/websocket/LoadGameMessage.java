package client.websocket;

import chess.ChessGame;
import model.Game;
import ui.DrawBoard;
import websocket.messages.ServerMessage;

public class LoadGameMessage implements NotificationHandler {

    @Override
    public void notify(ServerMessage notification) {
        ChessGame game = notification.getGame();//draw board?

    }
}
