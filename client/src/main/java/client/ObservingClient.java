package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import model.Auth;
import ui.DrawBoard;

import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.Scanner;

public class ObservingClient {
    private static ChessGame game;
    private static String helpMessage = "Options (not case sensative):\n" +
            "Redraw the board: 'redraw'\n" +
            "Leave game: 'leave'\n" +
            "Help remembering commands: 'help'\n";

    public ObservingClient(int gamePlayID, Auth auth, ServerFacade serv, WebSocketFacade ws) {
        System.out.print(helpMessage);
        ws.connect(auth.authToken(),gamePlayID);
        game = ws.getGameBoard();
        new DrawBoard(false,ws.getGameBoard(),new ChessPosition(0,0));
        Scanner scanner = new Scanner(System.in);
        String playingPrompt = "GAME >>";//Change GAME to be the game name?
        var command = "";
        while (!command.equals("leave")) {
            System.out.print(playingPrompt);
            String line = scanner.nextLine();
            command = line.toLowerCase();
            if (command.equals("help")) {
                System.out.print(helpMessage);
            } else if (command.equals("redraw")) {
                game = ws.getGameBoard();
                new DrawBoard(false,ws.getGameBoard(),new ChessPosition(0,0));
            } else if (!command.equals("leave")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
        ws.leave(auth.authToken(),gamePlayID);
    }
}
