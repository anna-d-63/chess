package chess.piececalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMoveCalculator {
    public PieceMoveCalculator(){}

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};

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

    public Collection<ChessMove> inOneDir(ChessBoard board, ChessPosition myPosition, boolean diagonal){
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        boolean legal;
        boolean canContinue;
        ChessPosition myPos;
        ChessPosition newPos;

        for(int i = 0; i < 4; i++) {
            canContinue = true;
            myPos = myPosition;
            while (canContinue) {
                if (diagonal) {
                    newPos = moveOneSquare(myPos, dirs[i], dirs[i + 1]);
                } else {
                    newPos = moveOneSquare(myPos, dirs[i], null);
                }
                legal = legalMove(board, newPos, true, myColor);
                canContinue = continueOn(board, newPos);
                if (legal) {
                    legalMoves.add(new ChessMove(myPosition, newPos, null));
                    myPos = newPos;
                } else {
                    break;
                }
            }
        }

        return legalMoves;
    }

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
