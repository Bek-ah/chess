package client;

import com.google.gson.Gson;
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
        var request = buildRequest("PUT","/game",joinGame);
        var response = sendRequest(request);
        ArrayList<Game> gameList = new ArrayList<>();
        System.out.print(response);
    }
    public int createGame(String gameName) throws AccessDeniedException, HttpTimeoutException {
        Game newGame = new Game(null,gameName);
        var request = buildRequest("POST", "/game", gameName);
        var response = sendRequest(request);
        return response.statusCode();//it's returning 500 Internal Server error for newGame and gameName
    }
    public void deleteAll() throws AccessDeniedException {
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }
    public int register(String username, String password, String email) throws AccessDeniedException {
        User registerUser = new User(username, password, email);
        HttpRequest.BodyPublisher body = makeRequestBody(registerUser);
        var request = buildRequest("POST","/user", registerUser);
        var response = sendRequest(request);
        return response.statusCode();
    }
    public ArrayList<Game> getGames() throws AccessDeniedException, HttpTimeoutException {
        var request = buildRequest("GET","/game",null);
        var response = sendRequest(request);
        ArrayList<Game> gameList = new ArrayList<>();
        System.out.print(response);
        //gameList.add(response.body(), Game.class);
        return gameList;
    }
    public int login(String username, String password) throws AccessDeniedException {
        User loginUser = new User(username, password, null);
        var request = buildRequest("POST","/session", loginUser);
        var response = sendRequest(request);
        return response.statusCode();
    }
    public void logout() throws AccessDeniedException {
        var request = buildRequest("DELETE", "/session", null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null){
            request.setHeader("Content-Type", "application/json");
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
