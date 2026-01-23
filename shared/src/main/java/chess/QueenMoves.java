package chess;

import java.util.ArrayList;
import java.util.Objects;

public class QueenMoves {
    ArrayList<ChessPosition> possibleEndings = new ArrayList<ChessPosition>();
    ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();
    ChessPiece currentPiece;

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
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn());
        //Check Diagonals
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going down and left
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()-1);
            pos.setRow(pos.getColumn()-1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going up and right
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()+1);
            pos.setRow(pos.getColumn()+1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going up and left
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()+1);
            pos.setCol(pos.getColumn()-1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going down and right
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()-1);
            pos.setCol(pos.getColumn()+1);
        }
        //Check Lines
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going down
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()-1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going up
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setRow(pos.getRow()+1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going left
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setCol(pos.getColumn()-1);
        }
        pos.setRow(startPosition.getRow());
        pos.setCol(startPosition.getColumn());
        while(inBounds(pos) && isItAvailable(startBoard, pos)) { // add possible going right
            possibleEndings.add(new ChessPosition(pos.getRow(),pos.getColumn()));
            pos.setCol(pos.getColumn()+1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueenMoves that = (QueenMoves) o;
        return Objects.equals(possibleEndings, that.possibleEndings) && Objects.equals(currentPiece, that.currentPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibleEndings, currentPiece);
    }
    public boolean inBounds(ChessPosition subject) {
        if ((subject.myCol <= 8) && (subject.myRow <= 8) && (subject.myCol >= 0) && (subject.myRow >= 0)){
            return true;
        }
        return false;
    }

    public boolean isItAvailable(ChessBoard fightBoard, ChessPosition square) {
        if (fightBoard.getPiece(square) != null) {
            return false;
        }
        return true;
    }
}
