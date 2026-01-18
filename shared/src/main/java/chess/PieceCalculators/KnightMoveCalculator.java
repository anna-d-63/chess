package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator extends PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> legalMoves = new ArrayList<>();

        Direction[] dirSideways = {Direction.EAST, Direction.WEST};
        Direction[] dirUpDown = {Direction.NORTH, Direction.SOUTH};

        ChessPosition forward;
        ChessPosition diagonal;
        boolean legal;

        for(Direction dir : dirUpDown){
            forward = moveOneSquare(myPos, dir, null);
            diagonal = moveOneSquare(forward, dirSideways[0], dirSideways[1]);
            legal = legalMove(board, myPos, diagonal, true);
            if(legal){
                legalMoves.add(new ChessMove(myPos, diagonal, null));
            }
        }
        for(Direction dir : dirSideways){
            forward = moveOneSquare(myPos, dir, null);
            diagonal = moveOneSquare(forward, dirUpDown[0], dirUpDown[1]);
            legal = legalMove(board, myPos, diagonal, true);
            if(legal){
                legalMoves.add(new ChessMove(myPos, diagonal, null));
            }
        }
        return legalMoves;
    }
}
