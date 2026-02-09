package chess;

import java.util.ArrayList;

public class PawnMove {

    ArrayList<ChessMove> posMoves = new ArrayList<>();
    ChessBoard myBoard = new ChessBoard();

    public ArrayList<ChessMove> listMoves(ChessBoard board, ChessPosition startPos) {
        myBoard = board;
        ChessPosition pos = new ChessPosition(1, 1);
        boolean forward = false;
        pos.setRow(startPos.getRow());
        pos.setCol(startPos.getColumn());
        if(board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.WHITE){
            pos.setRow(startPos.getRow()+1);
            if (isEmpty(pos,board)){
                forward = true;
                addPromotions(pos,startPos);
            }
            pos.setRow(startPos.getRow()+1);
            pos.setCol(startPos.getColumn()+1);//diagonal right
            if (isAvailable(startPos,pos,board) && pos.inBounds(pos) && !isEmpty(pos,board)){
                addPromotions(pos,startPos);
            }
            pos.setRow(startPos.getRow()+1);
            pos.setCol(startPos.getColumn()-1);//diagonal left
            if (isAvailable(startPos,pos,board) && pos.inBounds(pos) && !isEmpty(pos,board)){
                addPromotions(pos,startPos);
            }
            ChessPosition forward2 = new ChessPosition(startPos.getRow()+2,startPos.getColumn());
            if (startPos.getRow()==2 && forward && isEmpty(forward2,board)){
                posMoves.add(new ChessMove(startPos, new ChessPosition(startPos.getRow()+2,startPos.getColumn()),null));
            }
        } else if (board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.BLACK){
            pos.setRow(startPos.getRow()-1);
            if (isEmpty(pos,board)){
                forward = true;
                addPromotions(pos,startPos);
            }
            pos.setRow(startPos.getRow()-1);
            pos.setCol(startPos.getColumn()+1);//diagonal right
            if (isAvailable(startPos,pos,board) && pos.inBounds(pos) && !isEmpty(pos,board)){
                addPromotions(pos,startPos);
            }
            pos.setRow(startPos.getRow()-1);
            pos.setCol(startPos.getColumn()-1);//diagonal left
            if (isAvailable(startPos,pos,board) && pos.inBounds(pos) && !isEmpty(pos,board)){
                addPromotions(pos,startPos);
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
        } else { return board.getPiece(pos).getTeamColor() != board.getPiece(start).getTeamColor();}
    }
    public boolean isEmpty(ChessPosition pos, ChessBoard board){
        return board.getPiece(pos) == null;
    }

    public void addPromotions(ChessPosition pos, ChessPosition startPos){
        ChessGame.TeamColor team = myBoard.getPiece(startPos).getTeamColor();
        boolean whitePromo = pos.getRow()==8;
        boolean isWhite = team == ChessGame.TeamColor.WHITE;
        boolean isBlack = team == ChessGame.TeamColor.BLACK;
        boolean blackPromo = pos.getRow()==1;
        if ((isWhite && !whitePromo) || (isBlack && !blackPromo)) {
            posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()),null));
        } else {
            posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.QUEEN));
            posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.KNIGHT));
            posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.BISHOP));
            posMoves.add(new ChessMove(startPos, new ChessPosition(pos.getRow(),pos.getColumn()), ChessPiece.PieceType.ROOK));
        }
    }

}
