package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor turn = TeamColor.WHITE;
    ChessBoard myBoard;
    boolean canCastle = true;

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
        if (posMoves.isEmpty()){
            return Collections.emptyList();
        }
        ChessGame imagination = new ChessGame();
        imagination.setBoard(myBoard);
        for (ChessMove m : posMoves){
            //in imagination board, make the move and see if it is in check
            if (m.getPromotionPiece()==null) {
                imagination.getBoard().addPiece(new ChessPosition(m.getEndPosition().getRow(), m.getEndPosition().getColumn()), new ChessPiece(myBoard.getPiece(m.getStartPosition()).getTeamColor(), myBoard.getPiece(m.getStartPosition()).getPieceType()));
                imagination.getBoard().removePiece(m.getStartPosition());
            } else {
                imagination.getBoard().addPiece(new ChessPosition(m.getEndPosition().getRow(), m.getEndPosition().getColumn()), new ChessPiece(myBoard.getPiece(m.getStartPosition()).getTeamColor(), m.getPromotionPiece()));
                imagination.getBoard().removePiece(m.getStartPosition());
            }
            if (!imagination.isInCheck(imagination.getBoard().getPiece(m.getEndPosition()).getTeamColor())){
                validPos.add(new ChessMove(startPosition,m.getEndPosition(),m.getPromotionPiece()));
            }
            //reset board for the next iteration
            imagination.setBoard(myBoard);
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
        if (myBoard.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("No piece here");
        }
        if (getTeamTurn()!=myBoard.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException("Not your turn");
        }
        //check if move is in validMoves
        if(validMoves(move.getStartPosition()).contains(move)) {
            //move piece
            if (move.getPromotionPiece() == null) {
                myBoard.addPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()), new ChessPiece(myBoard.getPiece(move.getStartPosition()).getTeamColor(), myBoard.getPiece(move.getStartPosition()).getPieceType()));
            } else {
                if(move.getPromotionPiece()!=null){
                    if(move.movedPieceType == ChessPiece.PieceType.KING){
                        canCastle = false;
                    }
                    myBoard.addPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()), new ChessPiece(myBoard.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece()));
                } else {
                    myBoard.addPiece(new ChessPosition(move.getEndPosition().getRow(), move.getEndPosition().getColumn()), new ChessPiece(myBoard.getPiece(move.getStartPosition()).getTeamColor(), myBoard.getPiece(move.getStartPosition()).getPieceType()));
                }
            }
            myBoard.myBoard.remove(move.getStartPosition());
            //next team's turn
            if (turn == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else if (turn == TeamColor.BLACK) {
                setTeamTurn(TeamColor.WHITE);
            }
        } else {
            throw new InvalidMoveException("Invalid Move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = findKing(teamColor, myBoard);
        for (ChessPosition p : myBoard.myBoard.keySet()){ // for each piece
            if (myBoard.getPiece(p).getTeamColor() != teamColor) { //on the enemy team
                Collection<ChessMove> possMoves = myBoard.getPiece(p).pieceMoves(myBoard, p);
                ArrayList<ChessPosition> possibilities = new ArrayList<>();
                for (ChessMove m : possMoves){
                    possibilities.add(m.getEndPosition());
                }
                if (possibilities.contains(king)){
                    return true;
                }
            }
        }
        return false;
    }

    public ChessPosition findKing(TeamColor team, ChessBoard tempBoard){
        for (ChessPosition p : tempBoard.myBoard.keySet()){
            if (tempBoard.getPiece(p).getTeamColor() == team){
                if (tempBoard.getPiece(p).getPieceType() == ChessPiece.PieceType.KING){
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
                    if (!validMoves(pos).isEmpty()) {  //if the piece can move
                        return false;                   //not in checkmate
                    }
                }
            }
            return true;
        }
        return false;
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
        myBoard.myBoard.clear();
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
