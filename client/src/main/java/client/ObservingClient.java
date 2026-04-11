package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Auth;
import ui.DrawBoard;

import java.net.http.HttpTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ObservingClient {
    private static ChessGame game;
    private static String helpMessage = "Options (not case sensative):\n" +
            "Redraw the board: 'redraw'\n" +
            "Leave game: 'leave'\n" +
            "Highlight Legal Moves: 'highlight' <position> " +
            "(position format: a1\n" +
            "Help remembering commands: 'help'\n";
    private ChessPosition inputToPosition(String input){
        int row = Character.getNumericValue(input.charAt(1));
        char colTemp = input.charAt(0);
        int col = colTemp - 'a' + 1;
        ChessPosition pos = new ChessPosition(row,col);
        return pos;
    }
    private boolean testInput(String input){
        if (input.length()>2){
            System.out.println("Error: please enter a valid position (ex: A1)");
            return false;
        } else if (input.isBlank()){
            System.out.println("Error: please enter a position");
            return false;
        } else if (!input.matches("[A-Za-z][1-8]")){
            System.out.println("Error: please enter column then row (ex: A1)");
            return false;
        }
        return true;
    }
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
            } else if (command.equals("highlight")){
                System.out.print("Highlight position: ");
                String highPos = scanner.nextLine();
                highPos.toLowerCase();
                if (!testInput(highPos)){
                    continue;
                }
                ChessPosition highlightHere = inputToPosition(highPos);
                new DrawBoard(false, ws.getGameBoard(), highlightHere);
            } else if (!command.equals("leave")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
        ws.leave(auth.authToken(),gamePlayID);
    }
}
