package chess.piececalculators;

import chess.*;

import java.util.Collection;

public abstract class PieceMoveCalculator {
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

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
