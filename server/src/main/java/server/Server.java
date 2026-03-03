package server;

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
import java.util.Map;
import java.util.MissingFormatArgumentException;
import java.util.NoSuchElementException;
//import static com.sun.tools.javac.jvm.ByteCodes.ret;

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
        javalin.post("/session", this::logout);

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
        String authHeader = ctx.header("Auth");
        Auth auth = new Gson().fromJson(authHeader, Auth.class);
        ListGamesHandler list = new ListGamesHandler(auth,dataAccess);
        System.out.println(list.getGameList());
        ctx.status(200);
        String bodyText = new Gson().toJson(list.getGameList());
        System.out.println(list.getGameList());
        ctx.result(bodyText);
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
            new LogoutHandler().logout(ctx.header("Auth"), dataAccess);
            ctx.status(200);
        } catch (MissingFormatArgumentException d) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message","Error: wrong number of arguments")));
        } catch (AccessDeniedException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message","Error: password or username is incorrect")));
        }
    }

}
