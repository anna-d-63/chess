package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class QueenMoveCalculator extends PieceMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> diagonalMoves = inOneDir(board, myPosition, true);
        Collection<ChessMove> horizontalMoves = inOneDir(board, myPosition, false);

        diagonalMoves.addAll(horizontalMoves);

        return diagonalMoves;
    }
}
