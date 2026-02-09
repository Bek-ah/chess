package chess;

import java.util.ArrayList;

public class QueenMoves {


    ArrayList<ChessMove> posMoves = new ArrayList<ChessMove>();

    public ArrayList<ChessMove> listMoves(ChessPosition startPos, ChessBoard board){
        ChessPosition pos = new ChessPosition(1,1);
        pos.setRow(startPos.getRow()+1);
        pos.setCol(startPos.getColumn());
        //STRAIGHT LINES
        posMoves.addAll(new RookMoves().listMoves(board, startPos));
        //DIAGONAL LINES
        posMoves.addAll(new BishopMoves().listMoves(board, startPos));
        return posMoves;
    }
}
