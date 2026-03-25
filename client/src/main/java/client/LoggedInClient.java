package client;

import java.nio.file.AccessDeniedException;
import java.util.Scanner;

public class LoggedInClient {
    private static String helpMessage = "Options (not case sensative):\n" +
            "Login: 'login' <USERNAME> <PASSWORD>\n" +
            "Register: 'register' <USERNAME> <PASSWORD> <EMAIL>\n" +
            "Exit chess program: 'quit'\n" +
            "Help remembering commands: 'help'\n";

    public LoggedInClient(String serverURL) throws AccessDeniedException {
        Scanner scanner = new Scanner(System.in);
        String loggedInPrompt = "LOGGED IN>>";
        var command = "";
        while (!command.equals("quit")) {
            System.out.print(loggedInPrompt);
            String line = scanner.nextLine();
            command = line.toLowerCase();
            if (command.equals("help")) {
                System.out.print(helpMessage);
            } else if (command.equals("logout")){
                ServerFacade serv = new ServerFacade(serverURL);
                serv.logout();
                break;
            } else if (command.equals("create")){
                System.out.print("create stub\n");
            } else if (command.equals("list")){
                System.out.print("list stub\n");
            } else if (command.equals("play")){
                System.out.print("play stub\n");
            } else if (command.equals("observe")){
                System.out.print("observe stub\n");
            } else if (!command.equals("quit")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
    }}
