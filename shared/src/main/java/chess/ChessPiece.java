package chess;
//Phase 0
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    PieceType myType;
    ChessGame.TeamColor myColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        myType = type;
        myColor = pieceColor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return myType == that.myType && myColor == that.myColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myType, myColor);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return myColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return myType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);
        switch (currentPiece.getPieceType()) {
            //CHECK THAT THE getCol setCol are matching
            case PieceType.PAWN: //promotion need updated
                return new PawnMove().listMoves(board, myPosition);
            case PieceType.BISHOP: //should be done //
                return new BishopMoves().listMoves(board, myPosition);
            case PieceType.ROOK: //should be done //
                return new RookMoves().listMoves(board, myPosition);
            case PieceType.KING: //should be done //
                return new KingMoves().listMoves(board, myPosition);
            case PieceType.QUEEN: //should be done//
                return new QueenMoves().listMoves(board, myPosition);
            case PieceType.KNIGHT: //should be done //
                return new KnightMoves().listMoves(board, myPosition);
            default:
                return Collections.emptyList();
        }
    }
}
