package client.websocket;

import websocket.messages.ServerMessage;

public class ErrorMessage implements NotificationHandler{
    @Override
    public void notify(ServerMessage notification) {
        System.out.println("Error: invalid command");
    }
}
