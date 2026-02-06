package chess;

import java.util.ArrayList;

public class KnightMoves {
    ArrayList<ChessPosition> possibleEndings = new ArrayList<ChessPosition>();
    ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>();

    public ArrayList<ChessPosition> listMoveEnds(ChessBoard startBoard, ChessPosition startPosition) {
        calculatePossibilities(startBoard, startPosition);
        return possibleEndings;
    }

    public ArrayList<ChessMove> listMoves(ChessBoard startBoard, ChessPosition startPosition) {
        possibleEndings = listMoveEnds(startBoard, startPosition);
        for (ChessPosition i : possibleEndings) {
            possibleMoves.add(new ChessMove(startPosition, i, null)); // figure out promotion
        }
        return possibleMoves;
    }

    public void calculatePossibilities(ChessBoard startBoard, ChessPosition startPosition) {
        ChessPosition pos = new ChessPosition(1, 1);
        pos.setRow(startPosition.getRow()+2);
        pos.setCol(startPosition.getColumn()+1);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible up
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
        pos.setRow(pos.getRow()-1);
        pos.setCol(pos.getColumn()+1);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible diagonal upper left
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
        pos.setRow(pos.getRow()-2);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible left
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
        pos.setRow(pos.getRow()-1);
        pos.setCol(pos.getColumn()-1);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible diagonal lower left
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
        pos.setCol(pos.getColumn()-2);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible down
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
        pos.setCol(pos.getColumn()-1);
        pos.setRow(pos.getRow()+1);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible diagonal lower right
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
        pos.setRow(pos.getRow()+2);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible right
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
        pos.setCol(pos.getColumn()+1);
        pos.setRow(pos.getRow()+1);
        if (inBounds(pos) && isItAvailable(startBoard, pos, startPosition)) { // add possible diagonal upper right
            possibleEndings.add(new ChessPosition(pos.getRow(), pos.getColumn()));
        }
    }
    public boolean inBounds(ChessPosition subject) {
        if ((subject.myCol <= 8) && (subject.myRow <= 8) && (subject.myCol >= 1) && (subject.myRow >= 1)){
            return true;
        }
        return false;
    }

    public boolean isItAvailable(ChessBoard fightBoard, ChessPosition square, ChessPosition start){
        if (fightBoard.getPiece(square) == null){
            return true;
        } else if (fightBoard.getPiece(square).getTeamColor() != fightBoard.getPiece(start).getTeamColor()){
            return true;
        }
        return false;
    }
}
