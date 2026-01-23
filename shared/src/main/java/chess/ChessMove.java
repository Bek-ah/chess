package chess;
//getPromotionPiece how to check if there needs to be a promotion

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
//Phase 0
    ChessPosition niceStart;
    ChessPosition epicEnd;
    ChessPiece.PieceType movedPieceType;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        niceStart = startPosition;
        epicEnd = endPosition;
        movedPieceType = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return niceStart;
    }
    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return epicEnd;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        ChessPiece.PieceType newType;
        if (movedPieceType == ChessPiece.PieceType.PAWN) {
            if ((epicEnd.getRow()==8) || (epicEnd.getRow()==1)) {
                // do I need to get input from the user here?
                return ChessPiece.PieceType.QUEEN;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(niceStart, chessMove.niceStart) && Objects.equals(epicEnd, chessMove.epicEnd) && movedPieceType == chessMove.movedPieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(niceStart, epicEnd, movedPieceType);
    }
} // do not create a subclass for each subclass, it will make it harder later
// instead of inheriting chess pieces just give it a color and type and create a different set of classes to calculate the moves
// ex: class king_moves, rooke_moves etc.
// pieceMoves() would look at what kind of piece and then go to the move calculator class
