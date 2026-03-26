package client;

import chess.ChessGame;
import model.Auth;
import ui.DrawBoard;

import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.Scanner;
import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class LoggedInClient {
    private static String helpMessage = "Options (not case sensative):\n" +
            "Logout: 'logout'\n" +
            "Create a new Game: 'create' <GAME NAME>\n" +
            "List Games (with their IDs): 'list'\n" +
            "Play a Game: 'play' <GAME ID>\n" +
            "Observe a Game: 'observe' <GAME ID>\n" +
            "Help remembering commands: 'help'\n";

    public LoggedInClient(String serverURL, Auth auth) throws AccessDeniedException, HttpTimeoutException {
        Scanner scanner = new Scanner(System.in);
        String loggedInPrompt = "LOGGED IN>>";
        var command = "";
        ServerFacade serv = new ServerFacade(serverURL);
        while (!command.equals("quit")) {
            System.out.print(loggedInPrompt);
            String line = scanner.nextLine();
            command = line.toLowerCase();
            if (command.equals("help")) {
                System.out.print(helpMessage);
            } else if (command.equals("logout")) {
                if (serv.logout(auth)==200) {
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
                serv.getGames(auth, null);
            } else if (command.equals("play")) {
                System.out.print("Please enter the gameID: ");
                Integer gameID;
                if (scanner.hasNextInt()) {
                    gameID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Select <BLACK> or <WHITE>: ");
                    String playerColor = scanner.nextLine().toUpperCase();
                    switch(playerColor) {
                        case "BLACK":
                            serv.joinGame(playerColor, gameID, auth);
                            new DrawBoard(true, new ChessGame());
                        case "WHITE":
                            serv.joinGame(playerColor, gameID, auth);
                            new DrawBoard(false, new ChessGame());
                        default:
                            System.out.println("Please type black or white");
                    }
                } else {
                    System.out.println("Error: Game ID must be a number");
                }
            } else if (command.equals("observe")) {
                System.out.print("Game ID: ");
                Integer id = scanner.nextInt();
                serv.getGames(auth, id);
                new DrawBoard(false, serv.getGames(auth, id).getGame());
            } else if (!command.equals("quit")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
    }
}
