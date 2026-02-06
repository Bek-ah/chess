package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor turn = TeamColor.WHITE;
    ChessBoard myBoard;

    public ChessGame() {
        myBoard = new ChessBoard();
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(myBoard, chessGame.myBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, myBoard);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> validPos = new ArrayList<>();
        if (myBoard.getPiece(startPosition)==null) {
            return null;
        }
        Collection<ChessMove> posMoves = myBoard.getPiece(startPosition).pieceMoves(myBoard,startPosition);
        ChessGame imagination = new ChessGame();
        imagination.setBoard(myBoard);
        for (ChessMove m : posMoves){
            //in imagination board, make the move and see if it is in check
            imagination.getBoard().myBoard.keySet().remove(m.getStartPosition());
            if (m.getPromotionPiece()==null) {
                imagination.getBoard().addPiece(new ChessPosition(m.getEndPosition().getRow(), m.getEndPosition().getRow()), new ChessPiece(myBoard.getPiece(m.getStartPosition()).getTeamColor(), myBoard.getPiece(m.getStartPosition()).getPieceType()));
            } else {
                imagination.getBoard().addPiece(new ChessPosition(m.getEndPosition().getRow(), m.getEndPosition().getRow()), new ChessPiece(myBoard.getPiece(m.getStartPosition()).getTeamColor(), m.getPromotionPiece()));
            }
            if (!isInCheck(getTeamTurn())){
                validPos.add(new ChessMove(startPosition,m.getEndPosition(),m.getPromotionPiece()));
            }
            //reset board for the next iteration
            imagination.setBoard(myBoard);
        }
        if (isInCheck(myBoard.getPiece(startPosition).getTeamColor())){
            return myBoard.getPiece(startPosition).pieceMoves(myBoard,startPosition);
        }
        return validPos;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (getTeamTurn()!=myBoard.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException("Not your turn");
        }
        //check if move is in validMoves
        //move piece
        if (move.getPromotionPiece()==null) {
            myBoard.addPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getRow()), new ChessPiece(myBoard.getPiece(move.getStartPosition()).getTeamColor(), myBoard.getPiece(move.getStartPosition()).getPieceType()));
        } else {
            myBoard.addPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getRow()), new ChessPiece(myBoard.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece()));
        }
        myBoard.myBoard.keySet().remove(move.getStartPosition());
        //next team's turn
        if (turn == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else if (turn == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = findKing(teamColor);
        for (ChessPosition p : myBoard.myBoard.keySet()){ // for each piece
            if (myBoard.getPiece(p).getTeamColor() != teamColor){ //on the enemy team
                for (ChessMove m : myBoard.getPiece(p).pieceMoves(myBoard,p)) // for each possible move they make
                    if (m.getEndPosition() == king){ //could it kill the king?
                        return true;
                    }
            }
        }
        return false;
    }

    public ChessPosition findKing(TeamColor team){
        for (ChessPosition p : myBoard.myBoard.keySet()){
            if (myBoard.getPiece(p).getTeamColor() == team){
                if (myBoard.getPiece(p).getPieceType() == ChessPiece.PieceType.KING){
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            for (ChessPosition pos : myBoard.myBoard.keySet()){
                if (myBoard.getPiece(pos).getTeamColor()==turn) { //if it is current player's piece
                    if (validMoves(pos).size() >= 1) {  //if the piece can move
                        return false;                   //not in checkmate
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            for (ChessPosition p : myBoard.myBoard.keySet()){
                if (myBoard.getPiece(p).getTeamColor() == teamColor){
                    if (!validMoves(p).isEmpty()){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (ChessPosition i : board.myBoard.keySet()){
            myBoard.addPiece(new ChessPosition(i.getRow(),i.getColumn()),new ChessPiece(board.getPiece(i).getTeamColor(), board.getPiece(i).myType));
        };
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }
}
