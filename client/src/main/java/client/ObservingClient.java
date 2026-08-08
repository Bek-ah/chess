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
    private volatile ChessGame game;
    private static String helpMessage =
            "_________________________________________\n" +
            "Options (not case sensative):\n" +
            "Redraw the board: 'redraw'\n" +
            "Leave game: 'leave'\n" +
            "Highlight Legal Moves: 'highlight' <position> " +
            "(position format: a1)\n" +
            "Help remembering commands: 'help'\n" +
            "_________________________________________\n";
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
    public ObservingClient(int gamePlayID, Auth auth, ServerFacade serv, WebSocketFacade ws, Scanner mainScanner) {

        System.out.print(helpMessage);
        ws.connect(auth.authToken(),gamePlayID);
        while (ws.getGameBoard() == null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Inturrupted sleep");
            }
        }
        game = ws.getGameBoard();
        Scanner scanner = mainScanner;
        String playingPrompt = "GAME>> ";//Change GAME to be the game name?
        String command = "";
        while (!command.equals("leave")) {
            String line = scanner.nextLine();
            command = line.toLowerCase();
            if (command.equals("help")) {
                System.out.print(helpMessage);
                System.out.print(playingPrompt);
            } else if (command.equals("redraw")) {
                game = ws.getGameBoard();
                new DrawBoard(false,ws.getGameBoard(),new ChessPosition(0,0));
                System.out.print(playingPrompt);
            } else if (command.equals("highlight")){
                System.out.print("Highlight position: ");
                String highPos = scanner.nextLine();
                highPos.toLowerCase();
                if (!testInput(highPos)){
                    continue;
                }
                ChessPosition highlightHere = inputToPosition(highPos);
                new DrawBoard(false, ws.getGameBoard(), highlightHere);
                System.out.print(playingPrompt);
            } else if (command.equals("leave")){
                ws.leave(auth.authToken(),gamePlayID);
                break;
            } else {
                System.out.print("Error: not a game command, type 'help' to find a list of valid commands\n");
            }
        }
    }
}
