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
    public String prettyPosition(ChessPosition position){
        Integer tempCol = position.getColumn();
        Integer tempRow = position.getRow();
        String row = tempRow.toString();
        String col = "Unknown";
        switch(tempCol){
            case 1:
                col = "A";
            case 2:
                col = "B";
            case 3:
                col = "C";
            case 4:
                col = "D";
            case 5:
                col = "E";
            case 6:
                col = "F";
            case 7:
                col = "G";
            case 8:
                col = "H";
        }
        return String.format(col + row);
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void error(String errorM, Session session, Integer gameID) throws IOException {
        try {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            notification.addErrorMessage(errorM);
            connections.selfBroadcast(session, notification, gameID);
        } catch (IOException ex) {
            throw new IOException("Error: throwing error");
        }
    }

    private void move(UserMoveCommand action, Session session) throws IOException {
        int gameID = action.getGameID();
        connections.add(session, gameID);
        ChessMove move = action.getMove();
        Game gameData = da.getGamebyGameID(gameID);
        chess.ChessGame game;
        if (gameData.getGame() == null){
            game = new ChessGame();
        } else {
            game = gameData.getGame();
        }
        if (gameData.getGame().getTeamTurn()==null) {
            error("Error: Game is over", session, gameID);
            return;
        }
        Auth auth = service.getAuthData(action.getAuthToken());
        String movingPlayer;
        try{
            movingPlayer = auth.username();
        } catch (NullPointerException n){
            error("Error: Unauthorized", session, gameID);
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
            error("Error: Unauthorized", session, gameID);
            return;
        } else if (gameData.getGame().getTeamTurn()==null) {
            error("Error: Game is over", session, gameID);
            return;
        } else if (!currentPlayer.equals(movingPlayer)){
            error("Error: Not your turn", session, gameID);
            return;
        } else if (game.validMoves(move.getStartPosition()).contains(move.getEndPosition())){
            error("Error: Invalid move", session, gameID);
            return;
        }
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e){
            error("Error: Invalid move", session, gameID);
            return;
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        String pos = prettyPosition(move.getEndPosition());
        String message = String.format("%s moved to " + pos + "\n", auth.username());
        notification.addMessage(message);
        connections.broadcast(session, notification, gameID);
        ChessGame.TeamColor opponent = ChessGame.TeamColor.WHITE;
        if (currentPlayerColor== ChessGame.TeamColor.WHITE){
            opponent = ChessGame.TeamColor.BLACK;
        }
        if (game.isInCheckmate(opponent)){
            var notificationWinner = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            String opName;
            if (opponent== ChessGame.TeamColor.BLACK){
                opName = gameData.getBlackUsername();
            } else {
                opName = gameData.getWhiteUsername();
            }
            String messageCheckmate = String.format("%s is in checkmate. Game over", opName);
            notificationWinner.addMessage(messageCheckmate);
            connections.broadcast(null, notificationWinner,gameID);
        } else if (game.isInCheck(opponent)){
            var notificationWinner = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            String opName;
            if (opponent== ChessGame.TeamColor.BLACK){
                opName = gameData.getBlackUsername();
            } else {
                opName = gameData.getWhiteUsername();
            }
            String messageCheck = String.format("%s is in check", opName);
            notificationWinner.addMessage(messageCheck);
            connections.broadcast(null, notificationWinner,gameID);
        }
        var notificationS = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        notificationS.updateGame(game);
        gameData.setGame(game);
        service.movePiece(gameData, da, action.getAuthToken());
        connections.broadcast(null, notificationS,gameID);
    }
    private void connect(UserGameCommand action, Session session) throws IOException {
        int gameID = action.getGameID();
        if (service.authenticate(action.getAuthToken())) {
            if (service.getGamebyGameID(gameID) != null) {
                Game game = service.getGamebyGameID(gameID);
                connections.add(session,gameID);
                var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
                var notificationOthers = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                if (game.getGame()==null){
                    notification.updateGame(new ChessGame());
                } else {
                    notification.updateGame(game.getGame());
                }
                String userName = da.getAuthbyToken(action.getAuthToken()).username();
                if (game.getWhiteUsername() == userName){
                    String message = String.format("%s has joined the game",game.getWhiteUsername());
                    notificationOthers.addMessage(message);
                } else if (game.getBlackUsername() == userName){
                    String message = String.format("%s has joined the game",game.getBlackUsername());
                    notificationOthers.addMessage(message);
                } else {
                    Auth auth = da.getAuthbyToken(action.getAuthToken());
                    String message = String.format("%s has joined the game",auth.username());
                    notificationOthers.addMessage(message);
                }
                connections.selfBroadcast(session, notification, gameID);
                connections.broadcast(session, notificationOthers,gameID);
            } else {
                connections.add(session,gameID);
                error("Error: Bad input", session, gameID);
            }
        } else {
            connections.add(session,gameID);
            error("Error: Unauthorized", session, gameID);
        }
    }
    private void exit(UserGameCommand action, Session session) throws IOException {
        //remove playerUsername from game, then update game
        Game game = service.getGamebyGameID(action.getGameID());
        Auth auth = service.getAuthData(action.getAuthToken());
        if (auth.username().equals(game.getWhiteUsername())){
            da.updatePlayers(null,game.getBlackUsername(),action.getGameID());
        } else {
            da.updatePlayers(game.getWhiteUsername(),null,action.getGameID());
        }
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        String message = String.format("%s left the game", auth.username());
        notification.addMessage(message);
        connections.broadcast(session, notification, game.getID());
        connections.remove(session, game.getID());
    }
    private void resign(UserGameCommand action, Session session) throws IOException {
        //put game over as true with resign function then update game
        int gameID = action.getGameID();
        connections.add(session,gameID);
        ChessGame game = service.getGamebyGameID(gameID).getGame();
        Game gameData = service.getGamebyGameID(gameID);
        Auth authData = service.getAuthData(action.getAuthToken());
        String user = authData.username();
        boolean isPlayer = Objects.equals(user, gameData.getWhiteUsername())
                || Objects.equals(user, gameData.getBlackUsername());
        if (!isPlayer){
            error("Error: observer can't resign", session, gameID);
            return;
        }
        if (gameData.getGame().getTeamTurn()!=null){
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            String message = String.format("%s resigned. Game over", authData.username());
            notification.addMessage(message);
            connections.broadcast(null, notification,gameID);
            game.resign(); //setTurnNull
            gameData.setGame(game); //nullTurnSetinGame
            da.updateGame(gameData.getGame(),gameID);
            service.movePiece(gameData, da, action.getAuthToken());
            var test = da.getGamebyGameID(gameID);
            ChessGame didItWork = test.getGame();
        } else {
            error("Error: game over", session, gameID);
        }
    }

}