package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
//Phase 0
    public ChessBoard() {
        //dictionary with a tuple? {(1,1),"EMPTY"}, {(1,2),"KING"}
        //array, 8x8 64 values harder math
        //maybe 2 arrays of 8 values?
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        throw new RuntimeException("Not implemented");
        // update dictionary position and piece
        // look at what the position format the tests give, interpret to position understood by chessboard
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
        // return value of position in chessboard

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        // getpiece() for all pieces (can hardcode both sides or do math ig)
    }
}
