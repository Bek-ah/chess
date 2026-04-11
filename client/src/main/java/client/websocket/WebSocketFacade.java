package client.websocket;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Auth;
import model.Game;
import ui.DrawBoard;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    String httpUrl;
    ChessGame gameBoard;
    Boolean isBlack = false;

    public ChessGame getGameBoard(){
        return gameBoard;
    }
    private Gson boardSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // ChessPosition map fix
        gsonBuilder.registerTypeAdapter(
                new TypeToken<HashMap<ChessPosition, ChessPiece>>(){}.getType(),
                (JsonDeserializer<HashMap<ChessPosition, ChessPiece>>) (json, type, ctx) -> {

                    HashMap<ChessPosition, ChessPiece> map = new HashMap<>();
                    JsonObject obj = json.getAsJsonObject();

                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {

                        String[] parts = entry.getKey().split(",");

                        ChessPosition pos = new ChessPosition(
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1])
                        );

                        ChessPiece piece = ctx.deserialize(entry.getValue(), ChessPiece.class);

                        map.put(pos, piece);
                    }

                    return map;
                }
        );


        return gsonBuilder.create();
    }

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
                    JsonObject obj = JsonParser.parseString(message).getAsJsonObject();

                    ServerMessage.ServerMessageType type =
                            new Gson().fromJson(obj.get("serverMessageType"), ServerMessage.ServerMessageType.class);
                    if (type.equals(ServerMessage.ServerMessageType.LOAD_GAME)){
                        Gson gson = boardSerializer();
                        ChessGame game = gson.fromJson(obj.get("game"), ChessGame.class);
                        gameBoard = game;
                        new DrawBoard(isBlack,gameBoard,new ChessPosition(0,0));
                    } else if (type.equals(ServerMessage.ServerMessageType.NOTIFICATION)){
                        ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                        System.out.println(notification.getServerMessage());
                    } else if (type.equals(ServerMessage.ServerMessageType.ERROR)){
                        ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                        System.out.println(notification.getErrorMessage());
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