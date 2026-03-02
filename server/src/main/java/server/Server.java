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
        service.deleteAllGames();
        ctx.status(200);
    }
    private String register(Context ctx) throws AlreadyBoundException {
        String ret = service.getUser(ctx.body());
        ctx.status(200);
        return ret;
    }
}
