package client;

import chess.ChessGame;
import model.Auth;
import model.Game;
import model.GameJson;
import ui.DrawBoard;

import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoggedInClient {
    private static String helpMessage = "Options (not case sensative):\n" +
            "Logout: 'logout'\n" +
            "Create a new Game: 'create' <GAME NAME>\n" +
            "List Games (with their IDs): 'list'\n" +
            "Play a Game: 'play' <GAME ID>\n" +
            "Observe a Game: 'observe' <GAME ID>\n" +
            "Help remembering commands: 'help'\n";

    public void joining(String playerColor, int gamePlayID, Auth auth, ServerFacade serv){
        if(playerColor.equals("BLACK")) {
            int response = serv.joinGame(playerColor, gamePlayID, auth);
            if (response == 200) {
                new DrawBoard(true, new ChessGame());
            }
        } else if (playerColor.equals("WHITE")) {
            int response = serv.joinGame(playerColor, gamePlayID, auth);
            if (response == 200){
                new DrawBoard(false, new ChessGame());
            }
        } else {
            System.out.println("Please type black or white");
        }
    }

    public LoggedInClient(String serverURL, Auth auth) throws AccessDeniedException, HttpTimeoutException {
        Scanner scanner = new Scanner(System.in);
        String loggedInPrompt = "LOGGED IN>>";
        var command = "";
        ServerFacade serv = new ServerFacade(serverURL);
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
                    int gameID = serv.createGame(gameName, auth);
                    if (gameID == 401) {
                        System.out.print("Error: unable to make new game\n");
                        System.out.print(gameID);
                    }
                } else if (command.equals("list")) {
                    HashMap<Integer, GameJson> gamesList = serv.getGames(auth, null);
                    System.out.println("index | gameID | gameName | whiteUsername | blackUsername");
                    for (int index = 1; index <= gamesList.size(); index++){
                        int gameID = gamesList.get(index).getGameID();
                        String gameName = gamesList.get(index).getGameName();
                        String whiteUsername = "No user";
                        String blackUsername = "No user";
                        if (gamesList.get(index).getWhiteUsername()!=null) {
                            whiteUsername = gamesList.get(index).getWhiteUsername();
                        }
                        if (gamesList.get(index).getBlackUsername()!=null) {
                            blackUsername = gamesList.get(index).getBlackUsername();
                        }
                        System.out.println(index + " | " + gameID + " | " + gameName + " | " + whiteUsername + " | " + blackUsername);
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
                    System.out.print("Game ID: ");
                    Integer id = scanner.nextInt();
                    serv.getGames(auth, id);
                    new DrawBoard(false, serv.getGames(auth, null).get(id).getGame());
                } else {
                    System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");}
        }
    }
}
