package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.Game;

import javax.management.Notification;
import java.util.*;

public class ChessClient {
    /*private String visitorName = null;
    private String state = "loggedOut";

    public ChessClient(String serverUrl) throws NoSuchElementException {
        ServerFacade server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ 240 Chess ♕");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    public void notify(Notification notification) {
        System.out.println(notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "signin" -> signIn(params);
                case "list" -> listPets();
                case "signout" -> signOut();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (NoSuchElementException ex) {
            return ex.getMessage();
        }
    }

    public String signIn(String... params) throws NoSuchElementException {
        if (params.length >= 1) {
            state = "loggedIn";
            visitorName = String.join("-", params);
            ws.enterPetShop(visitorName);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new NoSuchElementException("Expected: <yourname>");
    }

    public String listPets() throws NoSuchElementException {
        assertSignedIn();
        ArrayList<Game> gamesList = server.listPets();
        var result = new StringBuilder();
        var gson = new Gson();
        for (Pet pet : pets) {
            result.append(gson.toJson(pet)).append('\n');
        }
        return result.toString();
    }

    public String adoptPet(String... params) throws NoSuchElementException {
        assertSignedIn();
        if (params.length == 1) {
            try {
                int id = Integer.parseInt(params[0]);
                Pet pet = getPet(id);
                if (pet != null) {
                    server.deletePet(id);
                    return String.format("%s says %s", pet.name(), pet.sound());
                }
            } catch (NumberFormatException ignored) {
            }
        }
        throw new NoSuchElementException("Expected: <pet id>");
    }

    public String adoptAllPets() throws NoSuchElementException {
        assertSignedIn();
        var buffer = new StringBuilder();
        for (Pet pet : server.listPets()) {
            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
        }

        server.deleteAllPets();
        return buffer.toString();
    }

    public String signOut() throws NoSuchElementException {
        assertSignedIn();
        ws.leavePetShop(visitorName);
        state = "loggedOut";
        return String.format("%s left the shop", visitorName);
    }

    private Game getGame(int id) throws NoSuchElementException {
        for (Game game : server) {
            if (game.getID() == id) {
                return game;
            }
        }
        return null;
    }

    public String help() {
        if (state.equals("loggedOut")) {
            return """
                    Options:
                    - Login by typing "login" <USERNAME> <PASSWORD>
                    - Create an account by typing "register" <USERNAME> <PASSWORD> <EMAIL>
                    - Quit by typing "quit"
                    - See options again by typing "help"
                    """;
        }
        return """
                Options:
                - Logout by typing "logout"
                - Create an game by typing "create" <Game Name>
                - List all games (with Game IDs) by typing "list"
                - Join a game by typing "join" <Game ID> <BLACK or WHITE>
                - Observe a game by typing "observe" <Game ID>
                - See options again by typing "help"
                - Quit by typing "quit"
                """;
    }

    private void assertSignedIn() throws NullPointerException {
        if (state.equals("loggedOut")) {
            throw new NullPointerException("You are not logged in");
        }
    }*/
}
