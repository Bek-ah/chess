package server;

import io.javalin.*;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import model.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import passoff.exception.ResponseParseException;
import service.ChessService;
import service.ChessService.*;

import java.net.http.WebSocket;
import java.rmi.AlreadyBoundException;
//import static com.sun.tools.javac.jvm.ByteCodes.ret;

public class Server {
    private final Javalin javalin;
    private final ChessService service = new ChessService(new DataAccess());

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::deleteAllGames);
        javalin.post("/user", this::register);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
    private void deleteAllGames(Context ctx) throws ResponseParseException {
        new ClearHandler();
        ctx.status(200);
    }
    private void register(Context ctx) throws AlreadyBoundException {
        RegisterHandler reg = new RegisterHandler(ctx.bodyAsClass(User.class));
       // Context ret = reg.register(ctx.bodyAsClass(User.class));
        ctx.status(200);
       // return ret;
    }
    private void getGames(Context ctx){
        ListGamesHandler list = new ListGamesHandler(ctx.bodyAsClass(Auth.class));
        list.getGameList();
        ctx.status(200);
    }
}
