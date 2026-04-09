package client.websocket;
import client.ServerFacade;
import model.Auth;
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

    public void movePiece(String auth, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("problem with moving piece");
        }
    }

    public void connect(String auth, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("problem with connection");
        }
    }
    public void login(String auth, Integer gameID) {
        try {
            System.out.println("Trying login ws facade");
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameID);
            System.out.println("madeMessage");
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("problem with login ws facade");
        }
    }
    public void leave(String auth, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("problem with leave");
        }
    }
    public void resign(String auth, Integer gameID) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println("problem with resign");
        }
    }


}