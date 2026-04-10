package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.Auth;
import model.Game;
import model.User;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.websocket.api.Session;
import passoff.exception.ResponseParseException;
import service.ChessService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    ChessService service;
    DataAccess da;
    public WebSocketHandler(DataAccess dataAccess){
        service = new ChessService(dataAccess);
        da = dataAccess;

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
            UserMoveCommand actionMove = null;
            if (action.getCommandType()== UserGameCommand.CommandType.MAKE_MOVE){
                actionMove = new Gson().fromJson(ctx.message(), UserMoveCommand.class);
            }
            //action has getGameID(), getAuthToken(), and getCommandType
            switch (action.getCommandType()) {
                case MAKE_MOVE -> move(actionMove, ctx.session);
                case CONNECT -> connect(action, ctx.session);
                case LEAVE -> exit(action, ctx.session);
                case RESIGN -> resign(action, ctx.session);
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

    private void move(UserMoveCommand action, Session session) throws IOException {
        int gameID = action.getGameID();
        connections.add(session);
        ChessMove move = action.getMove();
        ChessGame game;
        Game gameData;
        gameData = da.getGamebyGameID(gameID);
        game = gameData.getGame();
        Auth auth = service.getAuthData(action.getAuthToken());
        String movingPlayer;
        try{
            movingPlayer = auth.username();
        } catch (NullPointerException n){
            error("Unauthorized", session);
            return;
        }
        movingPlayer = auth.username();
        String currentPlayer;
        ChessGame.TeamColor currentPlayerColor;
        if(game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)){
            currentPlayer = gameData.getWhiteUsername();
            currentPlayerColor = ChessGame.TeamColor.WHITE;
        } else {
            currentPlayer = gameData.getBlackUsername();
            currentPlayerColor = ChessGame.TeamColor.BLACK;
        }
        if (!service.authenticate(action.getAuthToken())){
            error("Unauthorized", session);
            return;
        } else if (game.getTeamTurn()==null) {
            error("Error: Game is over", session);
            return;
        } else if (!currentPlayer.equals(movingPlayer)){
            error("Not your turn", session);
            return;
        } else if (game.validMoves(move.getStartPosition()).contains(move.getEndPosition())){
            error("Invalid move", session);
            return;
        }
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e){
            error("Invalid move", session);
            return;
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage("Player moved");
        connections.broadcast(session, notification);
        ChessGame.TeamColor opponent = ChessGame.TeamColor.WHITE;
        if (currentPlayerColor== ChessGame.TeamColor.WHITE){
            opponent = ChessGame.TeamColor.BLACK;
        }
        if (game.isInCheckmate(opponent)){
            var notificationWinner = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notificationWinner.addMessage("You won!");
            connections.broadcast(null, notificationWinner);
        }
        var notificationS = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        notificationS.updateGame(game);
        gameData.setGame(game);
        service.movePiece(gameData, da, action.getAuthToken());
        connections.broadcast(null, notificationS);
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
        //remove playerUsername from game, then update game
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.addMessage("Player has left the game");
        connections.broadcast(session, notification);
        connections.remove(session);
    }
    private void resign(UserGameCommand action, Session session) throws IOException {
        //put game over as true with resign function then update game
        int gameID = action.getGameID();
        connections.add(session);
        ChessGame game = service.getGamebyGameID(gameID).getGame();
        Game gameData = service.getGamebyGameID(gameID);
        Auth authData = service.getAuthData(action.getAuthToken());
        String user = authData.username();
        boolean isPlayer = Objects.equals(user, gameData.getWhiteUsername()) || Objects.equals(user, gameData.getBlackUsername());
        if (!isPlayer){
            error("Error: observer can't resign", session);
            return;
        }
        if (game.getTeamTurn()!=null){
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notification.addMessage("Player resigned. Game over");
            connections.broadcast(null, notification);
            gameData.getGame().resign();
            service.movePiece(gameData, da, action.getAuthToken());
        } else if (game.getTeamTurn()==null) {
            error("Error: game over", session);
        }
    }

}