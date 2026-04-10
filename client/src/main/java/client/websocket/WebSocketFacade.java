package client.websocket;
import chess.ChessGame;
import chess.ChessMove;
import client.ServerFacade;
import model.Auth;
import model.Game;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    String httpUrl;
    ChessGame gameBoard;

    public WebSocketFacade(String url, Auth auth, NotificationHandler notificationHandler) {
        try {
            httpUrl = url;
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(notification);
                    if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
                        gameBoard = notification.getGame();
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println("problem with facade");
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void movePiece(String auth, Integer gameID, ChessMove move) {
        try {
            var action = new UserMoveCommand(UserMoveCommand.CommandType.MAKE_MOVE, auth, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("Error: problem with moving piece");
        }
    }

    public void connect(String auth, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("Error: problem with connection");
        }
    }

    public void leave(String auth, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("Error: problem with leave");
        }
    }
    public void resign(String auth, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("Error: problem with resign");
        }
    }


}