package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    int myRow;
    int myCol;

    public ChessPosition(int row, int col) {
        myRow = row;
        myCol = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return myRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return myCol;
    }

    public void setRow(int newRow) {
        myRow = newRow;
    }
    public void setCol(int newCol) {
        myCol = newCol;
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



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return myRow == that.myRow && myCol == that.myCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myRow, myCol);
    }
}
