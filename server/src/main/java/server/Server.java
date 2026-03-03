package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.sun.jdi.request.InvalidRequestStateException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.opentest4j.AssertionFailedError;
import passoff.exception.ResponseParseException;
import service.ChessService;

import java.nio.file.AccessDeniedException;
import java.rmi.AlreadyBoundException;
import java.util.Collection;
import java.util.Map;
import java.util.MissingFormatArgumentException;
import java.util.NoSuchElementException;

public class Server {
    private final Javalin javalin;
    private final DataAccess dataAccess = new DataAccess();
    private final ChessService service = new ChessService(dataAccess);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::deleteAllGames);
        javalin.post("/user", this::register);
        javalin.get("/game", this::getGames);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }
    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }
    public void stop() {
        javalin.stop();
    }
    public DataAccess getDataAccess(){
        return dataAccess;
    }
    private void joinGame(Context ctx) {
        try {
        String authToken = ctx.header("Authorization");
        JoinGameHandler join = new JoinGameHandler();
        join.joinGame(authToken, ctx.body(), dataAccess);
        ctx.status(200);
        } catch (WrongArgumentException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message","Error: bad request")));
        } catch (AssertionError a){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message","Error: bad request")));
        } catch (AlreadyBoundException b){
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message","Error: bad request")));
        }
    }
    private void createGame(Context ctx) throws ClassNotFoundException {
        try {
            String authToken = ctx.header("Authorization");
            String gameName = ctx.body();
            Game newGame = new CreateGameHandler().addGame(authToken,gameName,dataAccess);
            GameID gameID = new GameID(newGame.getID());
            String bodyText = new Gson().toJson(gameID);
            if (gameName.length() < 3 || authToken == null){
                ctx.status(400);
                ctx.result(new Gson().toJson(Map.of("message","Error: Bad request")));
            } else {
            ctx.status(200);
            ctx.result(bodyText);
            }
        } catch (ClassNotFoundException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message","Error: bad request")));
        } catch (WrongArgumentException b){
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message","Error: wrong parameters")));
        }

    }
    private void deleteAllGames(Context ctx) throws ResponseParseException {
        new ClearHandler(dataAccess);
        ctx.status(200);
    }
    private void register(Context ctx) throws AssertionFailedError, InvalidRequestStateException {
        try {
            Auth obj = new RegisterHandler().register(ctx.body(),dataAccess);
            //double register catch
            ctx.status(200);
            String bodyText = new Gson().toJson(obj);
            ctx.result(bodyText);
        } catch (AssertionFailedError e) {//not enough info catch
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message","Error: bad request")));
        } catch (AssertionError e) {//not enough info catch
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message","Error: not enough info")));
        }
    }
    private void getGames(Context ctx){
        try {
            String authHeader = ctx.header("Authorization"); //Parse

            ListGamesHandler list = new ListGamesHandler();
            Collection<Game> myList = list.getGameList(authHeader, dataAccess);
            for (Game game : myList){
                String gn = game.getName();
                String flattened = gn;
                if (gn != null && gn.startsWith("{") && gn.endsWith("}")) {
                    JsonObject json = JsonParser.parseString(gn).getAsJsonObject();
                    if (json.has("gameName")){
                        flattened = json.get("gameName").getAsString();
                    }
                    game.setName(flattened);
                }
            }
            Map<String, Collection<Game>> myMap = Map.of("games", myList);
            ctx.status(200);
            ctx.result(new Gson().toJson(myMap));
        } catch (AccessDeniedException v){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message","Error: unauthorized")));
        }
    }
    private void login(Context ctx) throws MissingFormatArgumentException, NoSuchElementException, AccessDeniedException {
        try{
            Auth obj = new LoginHandler().login(ctx.body(),dataAccess);
            ctx.status(200);
            ctx.result(new Gson().toJson(obj));
        } catch (MissingFormatArgumentException d) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message","Error: wrong number of arguments")));
        } catch (AccessDeniedException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message","Error: password or username is incorrect")));
        } catch (NoSuchElementException f){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message","Error: no such user found")));
        }
    }
    private void logout(Context ctx) throws MissingFormatArgumentException, NoSuchElementException, AccessDeniedException {
        try{
            new LogoutHandler().logout(ctx.header("Authorization"), dataAccess);
            ctx.status(200);
        } catch (NoSuchElementException d) {
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message","Error: unauthorized")));
        }
    }

}
