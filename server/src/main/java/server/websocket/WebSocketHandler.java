package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.User;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case MAKE_MOVE -> move(action.getGameID().toString(), ctx.session);
                case CONNECT -> connect(action.getGameID().toString(), ctx.session);
                case LEAVE -> exit(action.getGameID().toString(), ctx.session);
                case RESIGN -> resign(action.getGameID().toString(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void move(String visitorName, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification);
    }
    private void connect(String visitorName, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s has joined the game", visitorName);
        System.out.println(message);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(null, notification);
    }
    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(session, notification);
        connections.remove(session);
    }
    private void resign(String visitorName, Session session) throws IOException {
        var message = String.format("%s resigned", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(session, notification);
    }
    public void error() throws IOException {
        try {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            connections.broadcast(null, notification);
        } catch (IOException ex) {
            throw new IOException("Error: throwing error");
        }
    }
}