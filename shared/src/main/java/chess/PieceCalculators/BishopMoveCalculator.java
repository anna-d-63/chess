package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator extends PieceMoveCalculator{    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};

        boolean legal;
        ChessPosition myPos;

        for(int i = 0; i < 4; i++) {
            legal = true;
            myPos = myPosition;
            while (legal) {
                ChessPosition newPos = moveOneSquare(myPos, directions[i], directions[i+1]);
                legal = legalMove(board, myPos, newPos, true);
                if (legal) {
                    legalMoves.add(new ChessMove(myPos, newPos, null));
                    myPos = newPos;
                } else {
                    break;
                }
            }
        }

        return legalMoves;
    }
}
