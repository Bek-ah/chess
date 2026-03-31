package client;

import model.Auth;

import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.Scanner;

public class PlayingClient {

    private static String helpMessage = "Options (not case sensative):\n" +
            "Redraw the board: 'redraw'\n" +
            "Resign: 'resign'\n" +
            "Make a move: 'move' <starting position> <end position> (example of position: A1)\n" +
            "Leave game: 'leave'\n" +
            "Help remembering commands: 'help'\n";

    public PlayingClient(String playerColor, int gamePlayID, Auth auth, ServerFacade serv) {
        System.out.print(helpMessage);
        Scanner scanner = new Scanner(System.in);
        String playingPrompt = "GAME >>";//Change GAME to be the game name?
        var command = "";
        while (!command.equals("leave")) {
            System.out.print(playingPrompt);
            String line = scanner.nextLine();
            command = line.toLowerCase();
            if (command.equals("help")) {
                System.out.print(helpMessage);
            } else if (command.equals("resign")){
                System.out.println("resign stub");
            } else if (command.equals("redraw")){
                System.out.println("redraw stub");
            }  else if (command.equals("move")){
                System.out.print("move stub");
                String username = scanner.nextLine();
                System.out.print("move end: ");
                String endPos = scanner.nextLine();
            } else if (!command.equals("leave")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
    }
}
