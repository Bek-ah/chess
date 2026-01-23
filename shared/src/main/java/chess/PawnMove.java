package chess;
import java.util.ArrayList;
import java.util.Objects;

public class PawnMove {

    ArrayList<ChessPosition> possibleEndings = new ArrayList<ChessPosition>();
    ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
    ChessPiece currentPiece;

    public ArrayList<ChessPosition> listMoveEnds(ChessBoard startBoard, ChessPosition startPosition){
        calculatePossibilities(startBoard, startPosition);
        return possibleEndings;
    }
    public ArrayList<ChessMove> listMoves(ChessBoard startBoard, ChessPosition startPosition){
        possibleEndings = listMoveEnds(startBoard,startPosition);
        if (possibleEndings.contains(startPosition)){
            possibleMoves.add(new ChessMove(startPosition, startPosition, ChessPiece.PieceType.KING));
            possibleMoves.add(new ChessMove(startPosition, startPosition, ChessPiece.PieceType.QUEEN));
            possibleMoves.add(new ChessMove(startPosition, startPosition, ChessPiece.PieceType.BISHOP));
            possibleMoves.add(new ChessMove(startPosition, startPosition, ChessPiece.PieceType.ROOK));
            possibleMoves.add(new ChessMove(startPosition, startPosition, ChessPiece.PieceType.KNIGHT));
            possibleMoves.add(new ChessMove(startPosition, startPosition, ChessPiece.PieceType.PAWN));
            possibleEndings.remove(startPosition);
        }
        for (ChessPosition i : possibleEndings){
            possibleMoves.add(new ChessMove(startPosition, i,null)); // figure out promotion
        }
        return possibleMoves;
    }

    public void calculatePossibilities(ChessBoard startBoard, ChessPosition startPosition) {
        currentPiece = startBoard.getPiece(startPosition);
        if ((startPosition.getRow() != 8) || (startPosition.getRow() != 1) ) {
            if (startPosition.getRow() == 2) { // if first move of pawn
                ChessPosition posForward2 = new ChessPosition(3, startPosition.getColumn());
                if (inBounds(posForward2) && isItAvailable(startBoard, posForward2)) {
                    possibleEndings.add(posForward2);
                }
            }
            ChessPosition posForward = new ChessPosition((startPosition.getRow()+1),startPosition.getColumn());
            if (inBounds(posForward) && isItAvailable(startBoard, posForward)) {
                possibleEndings.add(posForward);
            }
        } else {
            //can promote
            possibleEndings.add(startPosition);
        }
        if (currentPiece.myColor.equals(ChessGame.TeamColor.WHITE)){
            possibleEndings.add(new ChessPosition(startPosition.getRow()+1,startPosition.getColumn()+1));
            possibleEndings.add(new ChessPosition(startPosition.getRow()+1,startPosition.getColumn()-1));
        }
        if (currentPiece.myColor.equals(ChessGame.TeamColor.BLACK)){
            possibleEndings.add(new ChessPosition(startPosition.getRow()-1,startPosition.getColumn()+1));
            possibleEndings.add(new ChessPosition(startPosition.getRow()-1,startPosition.getColumn()-1));
        }
    }

    public boolean inBounds(ChessPosition subject) {
        if ((subject.myCol <= 8) && (subject.myRow <= 8) && (subject.myCol >= 0) && (subject.myRow >= 0)){
            return true;
        }
        return false;
    }

    public boolean isItAvailable(ChessBoard fightBoard, ChessPosition square){
        if (fightBoard.getPiece(square) != null){
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PawnMove pawnMove = (PawnMove) o;
        return Objects.equals(possibleEndings, pawnMove.possibleEndings) && Objects.equals(currentPiece, pawnMove.currentPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleEndings, currentPiece);
    }
}
//WHAT IS THE DIFFERENCE BETWEEN ChessMove.java and ChessPiece method pieceMoves
//collection of valid moves
//executes the move chosen
//these classes are for possibilities, or ChessPiece.pieceMoves