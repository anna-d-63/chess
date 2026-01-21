package chess.PieceCalculators;

import chess.*;

import java.util.Collection;
import java.util.List;

public class PieceMoveCalculator {
    public PieceMoveCalculator(){}

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    public ChessPosition moveOneSquare(ChessPosition myPosition, Direction dir1, Direction dir2){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if(dir1 == Direction.NORTH || dir2 == Direction.NORTH){row++;}
        if(dir1 == Direction.EAST || dir2 == Direction.EAST){col++;}
        if(dir1 == Direction.SOUTH || dir2 == Direction.SOUTH){row--;}
        if(dir1 == Direction.WEST || dir2 == Direction.WEST){col--;}

        return new ChessPosition(row, col);
    }

    public boolean legalMove(ChessBoard board, ChessPosition newPos, boolean canCapture, ChessGame.TeamColor myColor){
        int row = newPos.getRow();
        int col = newPos.getColumn();

        if(row > 8 || row < 1 || col > 8 || col < 1){return false;} //can't move out of bounds

        ChessPiece incomingPiece = board.getPiece(newPos);
        if(incomingPiece != null){
            if(canCapture){
                return incomingPiece.getTeamColor() != myColor; //it's a legal move to capture the enemy color
            } else {return false;} //mostly for a pawn because they can't capture by going forward
        } else {return true;} //can move there if the space is empty
    }

    public boolean continueOn(ChessBoard board, ChessPosition newPos){
        int row = newPos.getRow();
        int col = newPos.getColumn();

        if(row > 8 || row < 1 || col > 8 || col < 1){return false;}
        return board.getPiece(newPos) == null;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if      (piece.getPieceType() == ChessPiece.PieceType.BISHOP)   {return new BishopMoveCalculator().pieceMoves(board, myPosition);}
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK)     {return new RookMoveCalculator().pieceMoves(board, myPosition);}
        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN)    {return new QueenMoveCalculator().pieceMoves(board, myPosition);}
        else if (piece.getPieceType() == ChessPiece.PieceType.KING)     {return new KingMoveCalculator().pieceMoves(board, myPosition);}
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT)   {return new KnightMoveCalculator().pieceMoves(board, myPosition);}
        else if (piece.getPieceType() == ChessPiece.PieceType.PAWN)     {return new PawnMoveCalculator().pieceMoves(board, myPosition);}
        else {return List.of();}
    }
}
