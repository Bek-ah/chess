package client.websocket;

import websocket.messages.ServerMessage;

public class LoadGameMessage implements NotificationHandler {

    @Override
    public void notify(ServerMessage notification) {
        System.out.println("\nLOAD_GAME");//draw board?
    }
}
