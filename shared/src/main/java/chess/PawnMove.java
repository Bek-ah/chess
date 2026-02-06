package chess;

import java.util.ArrayList;

public class PawnMove {

    ArrayList<ChessMove> posMoves = new ArrayList<ChessMove>();

    public ArrayList<ChessMove> listMoves(ChessPosition startPos, ChessBoard board) {
        ChessPosition pos = new ChessPosition(1, 1);
        boolean forward = false;
        pos.setRow(startPos.getRow());
        pos.setCol(startPos.getColumn());
        if(board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.WHITE){
            pos.setRow(startPos.getRow()+1);
            if (isEmpty(pos,board)){
                forward = true;
                if (pos.getRow()!=8) {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
                } else {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.QUEEN));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.KNIGHT));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.BISHOP));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.ROOK));
                }
            }
            pos.setRow(startPos.getRow()+1);
            pos.setCol(startPos.getColumn()+1);//diagonal right
            if (isAvailable(startPos,pos,board) && inBounds(pos) && !isEmpty(pos,board)){
                if (pos.getRow()!=8) {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
                } else {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.QUEEN));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.KNIGHT));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.BISHOP));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.ROOK));
                }
            }
            pos.setRow(startPos.getRow()+1);
            pos.setCol(startPos.getColumn()-1);//diagonal left
            if (isAvailable(startPos,pos,board) && inBounds(pos) && !isEmpty(pos,board)){
                if (pos.getRow()!=8) {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
                } else {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.QUEEN));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.KNIGHT));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.BISHOP));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.ROOK));
                }
            }
            ChessPosition forward2 = new ChessPosition(startPos.getRow()+2,startPos.getColumn());
            if (startPos.getRow()==2 && forward && isEmpty(forward2,board)){
                posMoves.add(new ChessMove(startPos, new ChessPosition(startPos.getRow()+2,startPos.getColumn()),null));
            }
        } else if (board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.BLACK){
            pos.setRow(startPos.getRow()-1);
            if (isEmpty(pos,board)){
                forward = true;
                if (pos.getRow()!=1) {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
                } else {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.QUEEN));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.KNIGHT));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.BISHOP));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.ROOK));
                }
            }
            pos.setRow(startPos.getRow()-1);
            pos.setCol(startPos.getColumn()+1);//diagonal right
            if (isAvailable(startPos,pos,board) && inBounds(pos) && !isEmpty(pos,board)){
                if (pos.getRow()!=1) {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
                } else {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.QUEEN));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.KNIGHT));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.BISHOP));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.ROOK));
                }
            }
            pos.setRow(startPos.getRow()-1);
            pos.setCol(startPos.getColumn()-1);//diagonal left
            if (isAvailable(startPos,pos,board) && inBounds(pos) && !isEmpty(pos,board)){
                if (pos.getRow()!=1) {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
                } else {
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.QUEEN));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.KNIGHT));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.BISHOP));
                    posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.ROOK));
                }
            }
            pos.setRow(startPos.getRow()-2);
            pos.setCol(startPos.getColumn());
            if (startPos.getRow()==7 && forward && isEmpty(pos,board)){
                posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
            }
        }
        return posMoves;
    }

    public boolean isAvailable(ChessPosition start, ChessPosition pos, ChessBoard board){
        if (board.getPiece(pos) == null){
            return true;
        } else if (board.getPiece(pos).getTeamColor() != board.getPiece(start).getTeamColor()){
            return true;
        }
        return false;
    }
    public boolean isEmpty(ChessPosition pos, ChessBoard board){
        if (board.getPiece(pos) == null){
            return true;
        }
        return false;
    }
    public boolean inBounds(ChessPosition pos){
        if(pos.getRow()>0 && pos.getColumn()>0 && pos.getRow()<9 && pos.getColumn()<9){
            return true;
        }
        return false;
    }
}
