package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.Game;
import model.User;
import org.eclipse.jetty.websocket.api.Session;
import service.ChessService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    ChessService service;
    public WebSocketHandler(DataAccess dataAccess){
        service = new ChessService(dataAccess);
    }

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

            //action has getGameID(), getAuthToken(), and getCommandType
            switch (action.getCommandType()) {
                case MAKE_MOVE -> move(action, ctx.session);
                case CONNECT -> connect(action, ctx.session);
                case LEAVE -> exit(action, ctx.session);
                case RESIGN -> resign(action.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void error(String errorM, Session session) throws IOException {
        try {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.addErrorMessage(errorM);
            connections.selfBroadcast(session, notification);
        } catch (IOException ex) {
            throw new IOException("Error: throwing error");
        }
    }

    private void move(UserGameCommand action, Session session) throws IOException {
        int gameID = action.getGameID();
        connections.add(session);
        var message = String.format("%s is in the shop", gameID);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification);
    }
    private void connect(UserGameCommand action, Session session) throws IOException {
        int gameID = action.getGameID();
        if (service.authenticate(action.getAuthToken())) {
            if (service.getGamebyGameID(gameID) != null) {
                Game game = service.getGamebyGameID(gameID);
                connections.add(session);
                var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                var notificationOthers = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                notification.updateGame(game.getGame());
                notificationOthers.addMessage("Player has joined the game");
                connections.selfBroadcast(session, notification);
                connections.broadcast(session, notificationOthers);
            } else {
                connections.add(session);
                error("Error: Bad input", session);
            }
        } else {
            connections.add(session);
            error("Error: Unauthorized", session);
        }
    }
    private void exit(UserGameCommand action, Session session) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage("Player has left the game");
        connections.broadcast(session, notification);
        connections.remove(session);
    }
    private void resign(Integer gameID, Session session) throws IOException {
        var message = String.format("%s resigned", gameID);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(session, notification);
    }

}