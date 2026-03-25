package client;

import java.util.Scanner;

public class LoggedOutClient {
    private static String helpMessage = "Options (not case sensative):\n" +
            "Login: 'login' <USERNAME> <PASSWORD>\n" +
            "Register: 'register' <USERNAME> <PASSWORD> <EMAIL>\n" +
            "Exit chess program: 'quit'\n" +
            "Help remembering commands: 'help'\n";

    public LoggedOutClient(String serverURL) {
        System.out.println("♕ 240 Chess Type 'help' to get started ♕");
        System.out.print(helpMessage);
        Scanner scanner = new Scanner(System.in);
        String loggedOutPrompt = "LOGGED OUT>>";
        var command = "";
        while (!command.equals("quit")) {
            System.out.print(loggedOutPrompt);
            String line = scanner.nextLine();
            command = line.toLowerCase();
            if (command.equals("help")) {
                System.out.print(helpMessage);
            } else if (command.equals("login")){
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                ServerFacade serv = new ServerFacade(serverURL);
                serv.login(username, password);
            }  else if (command.equals("register")){
                System.out.print("register stub\n");
            }  else {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
    }
}
