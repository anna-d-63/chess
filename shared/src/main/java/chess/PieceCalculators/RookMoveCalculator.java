package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator extends PieceMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> legalMoves = new ArrayList<>();

        boolean legal;

        for(Direction dir : Direction.values()) {
            legal = true;
            while (legal) {
                ChessPosition newPos = moveOneSquare(myPos, dir, null);
                legal = legalMove(board, myPos, newPos, true);
                if (legal) {
                    legalMoves.add(new ChessMove(myPos, newPos, null));
                } else {
                    break;
                }
            }
            return legalMoves;
        }
    }
}
