package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Auth;
import model.Game;
import model.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public void joinGame(String playerColor, Integer gameID) throws AccessDeniedException {
        Game joinGame = new Game(gameID, playerColor);
        var request = buildRequest("PUT","/game",joinGame, null);
        var response = sendRequest(request).statusCode();
        //Game game = response.game();
        System.out.print(response);
    }
    public int createGame(String gameName) throws AccessDeniedException, HttpTimeoutException {
        Game newGame = new Game(null,gameName);
        var request = buildRequest("POST", "/game", gameName, null);
        var response = sendRequest(request);
        return response.statusCode();//it's returning 500 Internal Server error for newGame and gameName
    }
    public void deleteAll() throws AccessDeniedException {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }
    public Auth register(String username, String password, String email) throws AccessDeniedException {
        User registerUser = new User(username, password, email);
        HttpRequest.BodyPublisher body = makeRequestBody(registerUser);
        var request = buildRequest("POST","/user", registerUser, null);
        String response = String.valueOf(sendRequest(request).headers().firstValue("Authorization"));
        Auth auth = new Auth(username, response);
        return auth;
    }
    public ArrayList<Game> getGames() throws AccessDeniedException, HttpTimeoutException {
        var request = buildRequest("GET","/game",null,  null);
        var response = sendRequest(request);
        ArrayList<Game> gameList = new ArrayList<>();
        System.out.print(response);
        //gameList.add(response.body(), Game.class);
        return gameList;
    }
    public Auth login(String username, String password) throws AccessDeniedException {
        User loginUser = new User(username, password, null);
        var request = buildRequest("POST","/session", loginUser, null);
        var response = sendRequest(request);
        Auth returnAuth;
        if (response.statusCode()!=200){
            returnAuth = new Auth(username, "");
        } else {
            JsonObject auth1 = JsonParser.parseString(response.body()).getAsJsonObject();
            String auth2 = auth1.get("authToken").getAsString();
            returnAuth = new Auth(username, auth2);
        }
        return returnAuth;
    }
    public void logout() throws AccessDeniedException {
        var request = buildRequest("DELETE", "/session", null, null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null){
            request.setHeader("Content-Type", "application/json");
            if (authToken != null) {
                request.setHeader("Authorization", authToken);
            }
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
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws HttpTimeoutException{
        var status = response.statusCode();
        if (!isSuccessful(status)){
            var body = response.body();
            if (body != null){
                throw new HttpTimeoutException("unable to handle response");
            }
        }
        return null;
    }
    public boolean isSuccessful(int status){ return status / 100 == 2; }
}
