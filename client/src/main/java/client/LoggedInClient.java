package client;

import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.Scanner;

public class LoggedInClient {
    private static String helpMessage = "Options (not case sensative):\n" +
            "Logout: 'logout'\n" +
            "Create a new Game: 'create' <GAME NAME>\n" +
            "List Games (with their IDs): 'list'\n" +
            "Play a Game: 'play' <GAME ID>\n" +
            "Observe a Game: 'observe' <GAME ID>\n" +
            "Help remembering commands: 'help'\n";

    public LoggedInClient(String serverURL) throws AccessDeniedException, HttpTimeoutException {
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
                Integer gameID = scanner.nextInt();
                System.out.print("Type <WHITE> or <BLACK>");
                String playerColor = scanner.nextLine();
                serv.joinGame(playerColor,gameID);
            } else if (command.equals("observe")){
                System.out.print("observe stub\n");
            } else if (!command.equals("quit")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
    }}
