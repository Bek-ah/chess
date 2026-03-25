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

    public LoggedInClient(String serverURL, Auth authToken) throws AccessDeniedException, HttpTimeoutException {
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
            } else if (command.equals("logout")){
                serv.logout();
                break;
            } else if (command.equals("create")){
                System.out.print("Game name: ");
                String gameName = scanner.nextLine();
                if (!serv.isSuccessful(serv.createGame(gameName))){
                    System.out.print("Error: unable to make new game\n");
                }
            } else if (command.equals("list")){
                serv.getGames();
            } else if (command.equals("play")){
                System.out.print("Please enter the gameID: ");
                Integer gameID = scanner.nextInt(); //scan as a string and check if it can be scanned to int
                scanner.nextLine();
                System.out.print("Select <BLACK> or <WHITE>: ");
                String playerColor = scanner.nextLine();
                if (playerColor.toUpperCase().equals("BLACK")){
                    serv.joinGame(playerColor,gameID);
                    new DrawBoard().DrawBoard(true, new ChessGame());
                } else if (playerColor.toUpperCase().equals("WHITE")) {
                    serv.joinGame(playerColor,gameID);
                    new DrawBoard().DrawBoard(false, new ChessGame());
                } else {
                    System.out.println("Please type black or white");
                }
            } else if (command.equals("observe")){
                new DrawBoard().DrawBoard(false, new ChessGame());
            } else if (!command.equals("quit")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
    }}
