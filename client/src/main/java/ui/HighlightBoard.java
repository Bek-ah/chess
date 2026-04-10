package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class HighlightBoard {
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 0;
    public static boolean blackView = false;
    public static ChessPosition startPos;
    public static Collection<ChessMove> highlightPos;
    public static void main(String[] args){
        new HighlightBoard(false, new ChessGame(), new ChessPosition(2,2));
    }
    public HighlightBoard(boolean blackPlayer, ChessGame game, ChessPosition startPos) {
        if (blackPlayer){
            blackView = true;
        }
        ChessPiece piece = game.getBoard().getPiece(startPos);
        highlightPos = piece.pieceMoves(game.getBoard(),startPos);
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        game.getBoard().resetBoard();

        out.print(ERASE_SCREEN);

        drawLetters(out);

        drawChessBoard(out, game.getBoard());

        drawLetters(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawLetters(PrintStream out) {
        String[] headers;
        setBlack(out);
        if (blackView == false) {
            headers = new String[]{"   ", " A ", " B ", " C ", " D ", " E ", " F ", " G ", " H", "   "};
        } else {
            headers = new String[]{"   ", " H ", " G ", " F ", " E ", " D ", " C ", " B ", " A", "   "};
        }
        for (int boardCol = 0; boardCol <= BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
            }
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard game) {
        String[] rows;
        if (blackView == true){
            rows = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", "   ", "   "};
        } else {
            rows = new String[]{" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ", "   ", "   "};
        }
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            printHeaderText(out, rows[boardRow]);
            drawRowOfSquares(out, game, boardRow+1);
            printHeaderText(out, rows[boardRow]);
            out.println();
            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                // Draw horizontal row separator.
                drawHorizontalLine(out);
                setBlack(out);
            }
        }
    }
    private static String pieceText(PrintStream out, ChessBoard game, ChessPosition pos){
        if (game.getPiece(pos)==null){
            return " ";
        }
        var piece = game.getPiece(pos).getPieceType();
        if (game.getPiece(pos).getTeamColor().equals(WHITE)){
            setWhite(out);
        } else if (game.getPiece(pos).getTeamColor().equals(BLACK)){
            setBlack(out);
        }
        switch(piece) {
            case KING:
                return "K";
            case QUEEN:
                return "Q";
            case BISHOP:
                return "B";
            case KNIGHT:
                return "N";
            case ROOK:
                return "R";
            case PAWN:
                return "P";
        }
        return "error";
    }
    private static void drawRowOfSquares(PrintStream out, ChessBoard game, Integer row) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                setWhite(out);
                ChessPosition currentPos = new ChessPosition(squareRow+1,boardCol+1);
                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    if (highlightPos.contains(currentPos)){
                        out.print(SET_BG_COLOR_RED);
                    } else if (((boardCol+row)%2)==1){
                        out.print(SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        out.print(SET_BG_COLOR_DARK_GREY);
                    }
                    if (blackView == true){
                        printPlayer(out, pieceText(out, game, new ChessPosition(row, 8-boardCol)));
                    } else {
                        printPlayer(out, pieceText(out, game, new ChessPosition(9-row, boardCol+1)));
                    }
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }
            }
        }
    }

    private static void drawHorizontalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setIDK(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private static void setIDK(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private static void printPlayer(PrintStream out, String piece) {
        out.print(" " + piece + " ");
        setWhite(out);
    }
}
