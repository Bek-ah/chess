package client.websocket;

import websocket.messages.ServerMessage;

public class NotificationMessage implements NotificationHandler{
    @Override
    public void notify(ServerMessage notification) {
        System.out.println("Reload Game to see changes");
    }
}
