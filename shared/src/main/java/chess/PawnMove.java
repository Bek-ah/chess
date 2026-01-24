package chess;
import java.util.ArrayList;
import java.util.Objects;

public class PawnMove {

    ArrayList<ChessPosition> possibleEndings = new ArrayList<ChessPosition>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PawnMove pawnMove = (PawnMove) o;
        return diagonal == pawnMove.diagonal && Objects.equals(possibleEndings, pawnMove.possibleEndings) && Objects.equals(possibleMoves, pawnMove.possibleMoves) && Objects.equals(currentPiece, pawnMove.currentPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleEndings, possibleMoves, currentPiece, diagonal);
    }

    ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
    ChessPiece currentPiece;
    boolean diagonal = false;

    public ArrayList<ChessPosition> listMoveEnds(ChessBoard startBoard, ChessPosition startPosition){
        calculatePossibilities(startBoard, startPosition);
        return possibleEndings;
    }
    public ArrayList<ChessMove> listMoves(ChessBoard startBoard, ChessPosition startPosition){
        calculatePossibilities(startBoard,startPosition);
        return possibleMoves;
    }

    public void calculatePossibilities(ChessBoard startBoard, ChessPosition startPosition) {
        currentPiece = startBoard.getPiece(startPosition);
        ChessPosition posForward;
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
             posForward = new ChessPosition((startPosition.getRow() + 1), startPosition.getColumn());
        } else { //if team black
            posForward = new ChessPosition((startPosition.getRow() - 1), startPosition.getColumn());
        }
        // Can you go forward 2 W and B
        ChessPosition posForward2 = new ChessPosition(0,0);
        if (startPosition.getRow() == 2 && currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            posForward2 = new ChessPosition(4, startPosition.getColumn());
        } else if (startPosition.getRow() == 7 && currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            posForward2 = new ChessPosition(5, startPosition.getColumn());
        }
        if (inBounds(posForward2) && isItAvailable(startBoard, posForward, startPosition) && isItAvailable(startBoard, posForward2, startPosition)) {
            possibleEndings.add(posForward2);
        }

        // Move Forward 1 W and B
        if (inBounds(posForward) && isItAvailable(startBoard, posForward, startPosition)) {
            possibleEndings.add(posForward);
        }
        // Diagonals
        ChessPosition posUL = new ChessPosition(0,0);
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            posUL = new ChessPosition(startPosition.getRow()+1, startPosition.getColumn()-1);
        } else {
            posUL = new ChessPosition(startPosition.getRow()-1, startPosition.getColumn()-1);
        }
        // Calculate Diagonals
        diagonal = true;
        if (inBounds(posUL) && isItAvailable(startBoard, posUL, startPosition)){
            possibleEndings.add(posUL);
        }
        diagonal = false;
        // Get next Diagonal UR
        ChessPosition posUR;
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            posUR = new ChessPosition(startPosition.getRow()+1, startPosition.getColumn()+1);
        } else {
            posUR = new ChessPosition(startPosition.getRow()-1, startPosition.getColumn()+1);
        }
        // Calculate DR
        diagonal = true;
        if (inBounds(posUR) && isItAvailable(startBoard, posUR, startPosition)){
            possibleEndings.add(posUR);
        }
        diagonal = false;
        // Add all endings to moves and add in promotions
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            for (ChessPosition i : possibleEndings){
                if (i.getRow() == 8){
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.KNIGHT));
                } else {
                    possibleMoves.add(new ChessMove(startPosition, i, null));
                }
            }
        }
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            for (ChessPosition i : possibleEndings){
                if (i.getRow() == 1){
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(startPosition, i, ChessPiece.PieceType.KNIGHT));
                } else {
                    possibleMoves.add(new ChessMove(startPosition, i, null));
                }
            }
        }
    }

    public boolean inBounds(ChessPosition subject) {
        if ((subject.myCol <= 8) && (subject.myRow <= 8) && (subject.myCol >= 1) && (subject.myRow >= 1)){
            return true;
        }
        return false;
    }

    public boolean isItAvailable(ChessBoard fightBoard, ChessPosition square, ChessPosition start){
        if (diagonal && fightBoard.getPiece(square) == null){
            return false;
        } else if (diagonal && (fightBoard.getPiece(square).getTeamColor() != fightBoard.getPiece(start).getTeamColor())){
            return true;
        } else if (!diagonal && fightBoard.getPiece(square) == null){
            return true;
        }
        return false;
    }

}
//WHAT IS THE DIFFERENCE BETWEEN ChessMove.java and ChessPiece method pieceMoves
//collection of valid moves
//executes the move chosen
//these classes are for possibilities, or ChessPiece.pieceMoves