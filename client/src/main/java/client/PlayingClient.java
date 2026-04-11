package client;

import chess.*;
import client.websocket.WebSocketFacade;
import model.Auth;
import model.Game;
import ui.DrawBoard;

import java.net.http.HttpTimeoutException;
import java.net.http.WebSocket;
import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.Scanner;

public class PlayingClient {

    private static String helpMessage = "Options (not case sensative):\n" +
            "Redraw the board: 'redraw'\n" +
            "Resign: 'resign'\n" +
            "Make a move: 'move' <start position> <end position> (example of position: A1)\n" +
            "Leave game: 'leave'\n" +
            "Highlight Legal Moves: 'highlight' <position>\n" +
            "Help remembering commands: 'help'\n";
    private DrawBoard drawBoard;
    private ChessPosition noHighlight = new ChessPosition(0,0);

    private ChessPosition inputToPosition(String input){
        int row = Character.getNumericValue(input.charAt(1));
        char colTemp = input.charAt(0);
        int col = colTemp - 'a' + 1;
        ChessPosition pos = new ChessPosition(row,col);
        return pos;
    }
    private boolean isBlack = false;
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

    public PlayingClient(String playerColor, int gamePlayID, Auth auth, ServerFacade serv, WebSocketFacade ws) {
        System.out.print(helpMessage);
        ws.connect(auth.authToken(),gamePlayID);
        if(playerColor.toLowerCase().equals("black")) {
            isBlack = true;
            new DrawBoard(isBlack, ws.getGameBoard(), noHighlight);
        } else {
            new DrawBoard(isBlack, ws.getGameBoard(), noHighlight);
        }
        Scanner scanner = new Scanner(System.in);
        String playingPrompt = "GAME >>";//Change GAME to be the game name?
        var command = "";
        //set drawBoard = new DrawBoard(isBlack, getOneGame());
        while (!command.equals("leave")) {
            System.out.print(playingPrompt);
            String line = scanner.nextLine();
            command = line.toLowerCase();
            if (command.equals("help")) {
                System.out.print(helpMessage);
            } else if (command.equals("resign")){
                System.out.print("Type y to confirm resignation");
                String confirm = scanner.nextLine().toLowerCase();
                if (confirm.equals("y")) {
                    ws.resign(auth.authToken(), gamePlayID);
                }
            } else if (command.equals("redraw")){
                new DrawBoard(isBlack, ws.getGameBoard(), noHighlight);
            } else if (command.equals("highlight")){
                System.out.print("Highlight position: ");
                String highPos = scanner.nextLine();
                highPos.toLowerCase();
                if (!testInput(highPos)){
                    continue;
                }
                ChessPosition highlightHere = inputToPosition(highPos);
                new DrawBoard(isBlack, ws.getGameBoard(), highlightHere);
            } else if (command.equals("move")){
                System.out.print("Piece position: ");
                String startPos = scanner.nextLine().toLowerCase();
                if (!testInput(startPos)){
                    continue;
                }
                System.out.print("Move to: ");
                String endPos = scanner.nextLine();
                if (!testInput(endPos)){
                    continue;
                }
                ChessPosition startpos = inputToPosition(startPos);
                ChessPosition endpos = inputToPosition(endPos);
                ChessGame gameBoard = ws.getGameBoard();
                Collection <chess.ChessMove> validMoveList = gameBoard.validMoves(startpos);
                ChessPiece.PieceType promoPiece = null;
                boolean isPawn = gameBoard.getBoard().getPiece(startpos)
                        .getPieceType().equals(ChessPiece.PieceType.PAWN);
                if ((endpos.getRow()==8) || (endpos.getRow()==1)) {
                    if (isPawn) {
                        System.out.print("Promote to: " +
                                "N - knight, Q - queen,\n" +
                                " R - rook, B - bishop\n");
                        String promotionPiece = scanner.nextLine().toLowerCase();
                        switch(promotionPiece){
                            case "n":
                                promoPiece = ChessPiece.PieceType.KNIGHT;
                                break;
                            case "q":
                                promoPiece = ChessPiece.PieceType.QUEEN;
                                break;
                            case "r":
                                promoPiece = ChessPiece.PieceType.ROOK;
                                break;
                            case "b":
                                promoPiece = ChessPiece.PieceType.BISHOP;
                                break;
                            default:
                                System.out.print("Error: Please type n, r, q, or b");
                        }
                    }
                }
                ChessMove move = new ChessMove(startpos,endpos,promoPiece);
                ws.movePiece(auth.authToken(), gamePlayID, move);
            } else if (!command.equals("leave")) {
                System.out.print("Error: not a command, type 'help' to find a list of valid commands\n");
            }
        }
        ws.leave(auth.authToken(),gamePlayID);
    }
}
