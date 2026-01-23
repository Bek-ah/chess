package chess;
//Phase 0
import java.util.ArrayList;
import java.util.Objects;

public class RookMoves {

    ArrayList<ChessPosition> possibleEndings = new ArrayList<ChessPosition>();
    ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
    ChessPiece currentPiece;
    boolean captured = false;

    public ArrayList<ChessPosition> listMoveEnds(ChessBoard startBoard, ChessPosition startPosition){
        calculatePossibilities(startBoard, startPosition);
        return possibleEndings;
    }
    public ArrayList<ChessMove> listMoves(ChessBoard startBoard, ChessPosition startPosition){
        possibleEndings = listMoveEnds(startBoard,startPosition);
        for (ChessPosition i : possibleEndings){
            possibleMoves.add(new ChessMove(startPosition, i,null)); // figure out promotion
        }
        return possibleMoves;
    }

    public void calculatePossibilities(ChessBoard startBoard, ChessPosition startPosition) {
        ChessPosition pos = new ChessPosition(1,1);
        pos.setRow(startPosition.getRow()-1);
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && !captured && isItAvailable(startBoard, pos, startPosition)) { // add possible going down
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()-1);
        }
        captured = false;
        pos.setRow(startPosition.getRow()+1);
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && !captured && isItAvailable(startBoard, pos, startPosition)) { // add possible going up
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()+1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn()-1);
        captured = false;

        while(inBounds(pos) && !captured && isItAvailable(startBoard, pos, startPosition)) { // add possible going left
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setCol(pos.getColumn()-1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn()+1);
        captured = false;

        while(inBounds(pos) && !captured && isItAvailable(startBoard, pos, startPosition)) { // add possible going right
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setCol(pos.getColumn()+1);
        }
    }

    public boolean inBounds(ChessPosition subject) {
        if ((subject.myCol <= 8) && (subject.myRow <= 8) && (subject.myCol >= 1) && (subject.myRow >= 1)){
            return true;
        }
        return false;
    }

    public boolean isItAvailable(ChessBoard fightBoard, ChessPosition square, ChessPosition start){
        if (fightBoard.getPiece(square) == null){ // is empty?
            return true;
        } else if (fightBoard.getPiece(square).getTeamColor() != fightBoard.getPiece(start).getTeamColor()){ // is enemy?
            captured = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RookMoves rookMoves = (RookMoves) o;
        return Objects.equals(possibleEndings, rookMoves.possibleEndings) && Objects.equals(currentPiece, rookMoves.currentPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleEndings, currentPiece);
    }
}

