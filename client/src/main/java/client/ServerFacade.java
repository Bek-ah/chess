package client;

import com.google.gson.*;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private int index;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public int joinGame(String playerColor, Integer gameID, Auth auth) {
        try {//change Game to GameJson more
            HashMap<Integer, GameJson> gameList = getGames(auth, gameID);
            GameJson gameToJoin = gameList.get(gameID);
            JoinGameInput body = new JoinGameInput(playerColor,gameToJoin.getGameID());
            var request = buildRequest("PUT", "/game", body, auth.authToken());
            Gson gson = new Gson();
            var response = sendRequest(request);
            //JsonObject games = JsonParser.parseString(response.body()).getAsJsonObject();
            //Game joinedGame = gson.fromJson(games,Game.class);
            return response.statusCode();
        } catch (AccessDeniedException e){
            System.out.println("Error: unauthorized");
        }
        return 0;
    }
    public int createGame(String gameName, Auth auth) {
        try {
            if (gameName.isEmpty()){
                return 401;
            }
            Map<String, String> gameName2 = Map.of("gameName", gameName);
            var request = buildRequest("POST", "/game", gameName2, auth.authToken());
            HttpResponse<java.lang.String> response = sendRequest(request);
            JsonObject auth1 = JsonParser.parseString(response.body()).getAsJsonObject();
            int auth3 = auth1.get("gameID").getAsInt();
            int status = response.statusCode();
            return auth3;
        } catch (AccessDeniedException e) {
            System.out.println("Error: Access denied");
        }
        return 401;
    }
    public Auth register(String username, String password, String email) {
        try {
            User registerUser = new User(username, password, email);
            var request = buildRequest("POST", "/user", registerUser, null);
            var response = sendRequest(request);
            Auth returnRegAuth;
            if (response.statusCode() != 200) {
                String errorCode = "" + response.statusCode();
                returnRegAuth = new Auth(errorCode, "");
            } else {
                JsonObject authReg = JsonParser.parseString(response.body()).getAsJsonObject();
                String authReg2 = authReg.get("authToken").getAsString();

                returnRegAuth = new Auth(username, authReg2);
            }
            return returnRegAuth;
        } catch (AccessDeniedException e) {
            System.out.println("Error: Access denied");
        }
        return null;
    }
    public HashMap<Integer, GameJson> getGames(Auth auth, Integer getGameId) {
        try {
            var request = buildRequest("GET", "/game", null, auth.authToken());
            var response = sendRequest(request);
            HashMap<Integer, GameJson> gameList = new HashMap<>();
            if (response.statusCode() != 200) {
                System.out.println("Error: Unauthorized");
                return null;
            } else {
                JsonObject games = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray gameList1 = games.getAsJsonArray("games");
                index = 0;
                for (JsonElement game : gameList1) {
                    index += 1;
                    Gson gson = new Gson();
                    JsonObject g = game.getAsJsonObject();
                    GameJson gameElement = gson.fromJson(g, GameJson.class);
                    gameList.put(index, gameElement);
                    int id = g.get("gameID").getAsInt();
                    String gn = g.get("gameName").getAsString();
                    if (getGameId == null){
                        continue;
                    }
                    if (getGameId == id) {
                        Gson gson2 = new Gson();
                        JsonElement gameRet1 = g.get("game");
                        //String gameRet2 = gameRet1.getAsString();
                        Game gameRet = gson2.fromJson(gameRet1, Game.class);
                        return gameList;
                    }
                }
                return gameList;
            }
        } catch (AccessDeniedException e) {
            System.out.println("Error: Access Denied");
        }
        return null;
    }
    public Auth login(String username, String password) {
        try {
            User loginUser = new User(username, password, null);
            var request = buildRequest("POST", "/session", loginUser, null);
            var response = sendRequest(request);
            Auth returnAuth;
            if (response.statusCode() != 200) {
                returnAuth = new Auth(username, "");
            } else {
                JsonObject auth1 = JsonParser.parseString(response.body()).getAsJsonObject();
                String auth2 = auth1.get("authToken").getAsString();
                returnAuth = new Auth(username, auth2);
            }
            return returnAuth;
        } catch (AccessDeniedException e) {
            System.out.println("Error: Access Denied");
        }
        return null;
    }
    public int logout(Auth auth) {
        var request = buildRequest("DELETE", "/session", null, auth.authToken());
        int status;
        try{
            status = sendRequest(request).statusCode();
        } catch (AccessDeniedException e){
            System.out.print("Error: Access denied");
            status = 401;
        }
        return status;
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null){
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }
    private HttpRequest.BodyPublisher makeRequestBody(Object request){
        if (request != null){
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }
    private HttpResponse<String> sendRequest(HttpRequest request) throws AccessDeniedException{
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex){
            throw new AccessDeniedException("HttpResponse failed");
        }
    }
    public boolean isSuccessful(int status){ return status / 100 == 2; }
}
