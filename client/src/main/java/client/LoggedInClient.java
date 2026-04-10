package client;

import chess.ChessGame;
import client.websocket.LoadGameMessage;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.Auth;
import model.GameJson;
import ui.DrawBoard;

import java.util.HashMap;
import java.util.Scanner;

public class LoggedInClient {
    private final WebSocketFacade ws;
    private static String helpMessage = "Options (not case sensative):\n" +
            "Logout: 'logout'\n" +
            "Create a new Game: 'create' <GAME NAME>\n" +
            "List Games (with their IDs): 'list'\n" +
            "Play a Game: 'play' <GAME ID>\n" +
            "Observe a Game: 'observe' <GAME ID>\n" +
            "Help remembering commands: 'help'\n";
    public String setW(String test){
        if (test != null){
            return test;
        }
        return "No user";
    }
    public void joining(String playerColor, int gamePlayID, Auth auth, ServerFacade serv){
        if(playerColor.equals("BLACK")) {
            int response = serv.joinGame(playerColor, gamePlayID, auth);
            if (response == 200) {
                new PlayingClient(playerColor, gamePlayID, auth, serv, ws);
            } else if (response != 30) {
                System.out.println("player taken"); //why else if (response !=30) for black and just else for white? possible bug
            }
        } else if (playerColor.equals("WHITE")) {
            int response = serv.joinGame(playerColor, gamePlayID, auth);
            if (response == 200){
                System.out.println("response 200: Attempting ws connection:");
                new PlayingClient(playerColor, gamePlayID, auth, serv, ws);
            } else {
                System.out.println("Color not available");
            }
        } else {
            System.out.println("Please type black or white");
        }
    }
    public int observing(Scanner scanner, ServerFacade serv, Auth auth){
        try {
            System.out.print("Game Number: ");
            int id = 0;
            if (scanner.hasNextInt()) {
                id = scanner.nextInt();
            } else {
                System.out.println("Error: please enter the game number");
                return 300;
            }
            if (id < 1){
                System.out.println("Error: please enter the game number");
                return 301;
            }

            HashMap<Integer, GameJson> list = serv.getGames(auth, id);
            //new DrawBoard(false, serv.getGames(auth, null).get(id).getGame());
            if (list.size() < id) {
                System.out.println("Error: please enter the correct game number");
                return 302;
            }
            new ObservingClient(id, auth, serv, ws);
            return 0;
        } catch (NullPointerException n) {
            System.out.println("Please enter a valid game number");
            return 300;
        }
    }

    public LoggedInClient(String serverURL, Auth auth) {
        Scanner scanner = new Scanner(System.in);
        String loggedInPrompt = "LOGGED IN>>";
        var command = "";
        ServerFacade serv = new ServerFacade(serverURL);
        NotificationHandler notify = new LoadGameMessage();
        ws = new WebSocketFacade(serverURL, auth, notify);
        while (!command.equals("quit")) {
            System.out.print(loggedInPrompt);
            String line = scanner.nextLine();
            command = line.toLowerCase();
                if(command.equals("help")) {
                    System.out.print(helpMessage);
                } else if (command.equals("logout")) {
                    if (serv.logout(auth) == 200) {
                        break;
                    } else {
                        System.out.println("Error: Already logged out");
                    }
                } else if (command.equals("create")) {
                    System.out.print("Game name: ");
                    String gameName = scanner.nextLine();
                    if (gameName.isBlank()){
                        System.out.print("Error: cannot be blank");
                    }
                    int gameID = serv.createGame(gameName, auth);
                    if (gameID == 401) {
                        System.out.print("Error: unable to make new game\n");
                        System.out.print(gameID);
                    }
                } else if (command.equals("list")) {
                    HashMap<Integer, GameJson> gamesList = serv.getGames(auth, null);
                    System.out.println("Game Number | Game Name | White Player | Black Player");
                    for (int index = 1; index <= gamesList.size(); index++){
                        int gameID = gamesList.get(index).getGameID();
                        String gameName = gamesList.get(index).getGameName();
                        String whiteUsername = "No user";
                        String blackUsername = "No user";
                        whiteUsername = setW(gamesList.get(index).getWhiteUsername());
                        blackUsername = setW(gamesList.get(index).getBlackUsername());
                        System.out.println(index + " | " + gameName + " | " + whiteUsername + " | " + blackUsername);
                    }
                } else if (command.equals("play")) {
                    System.out.print("Please enter the gameID: ");
                    Integer gamePlayID;
                    if (scanner.hasNextInt()) {
                        gamePlayID = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Select <BLACK> or <WHITE>: ");
                        String playerColor = scanner.nextLine().toUpperCase();
                        joining(playerColor, gamePlayID, auth, serv);
                    } else {
                        System.out.println("Error: Game ID must be a number");
                    }
                } else if (command.equals("observe")) {
                    observing(scanner,serv,auth);
                } else if (!command.equals("quit")) {
                    System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");}
        }
    }
}
