package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator extends PieceMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPos).getTeamColor();

        Direction[] dirSideways = {Direction.EAST, Direction.WEST};
        Direction[] dirUpDown = {Direction.NORTH, Direction.SOUTH};

        moveThatKnight(board, myPos, legalMoves, dirSideways, dirUpDown, myColor);
        moveThatKnight(board, myPos, legalMoves, dirUpDown, dirSideways, myColor);

        return legalMoves;
    }

    public void moveThatKnight(ChessBoard board, ChessPosition myPos, Collection<ChessMove> legalMoves,
                               Direction[] firstDirs, Direction[] secondDirs, ChessGame.TeamColor myColor){
        ChessPosition forward;
        ChessPosition diagonal;
        boolean legal;

        for(Direction dir : firstDirs){
            forward = moveOneSquare(myPos, dir, null);
            for(Direction dir2 : secondDirs) {
                diagonal = moveOneSquare(forward, dir, dir2);
                legal = legalMove(board, diagonal, true, myColor);
                if (legal) {
                    legalMoves.add(new ChessMove(myPos, diagonal, null));
                }
            }
        }
    }
}
