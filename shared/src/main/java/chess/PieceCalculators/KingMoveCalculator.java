package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator extends PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};

        ChessPosition newPos;
        boolean legal;

        for(Direction dir : Direction.values()) {
            newPos = moveOneSquare(myPos, dir, null);
            legal = legalMove(board, myPos, newPos, true);
            if(legal){
                legalMoves.add(new ChessMove(myPos, newPos, null));
            }
        }
        for(int i = 0; i < 4; i++) {
            newPos = moveOneSquare(myPos, directions[i], directions[i+1]);
            legal = legalMove(board, myPos, newPos, true);
            if(legal){
                legalMoves.add(new ChessMove(myPos, newPos, null));
            }
        }

        return legalMoves;
    }
}

