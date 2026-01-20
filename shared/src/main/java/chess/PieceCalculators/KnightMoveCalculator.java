package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator extends PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPos).getTeamColor();

        Direction[] dirSideways = {Direction.EAST, Direction.WEST};
        Direction[] dirUpDown = {Direction.NORTH, Direction.SOUTH};

        ChessPosition forward;
        ChessPosition diagonal;
        boolean legal;

        for(Direction dir : dirUpDown){
            forward = moveOneSquare(myPos, dir, null);
            for(Direction dir2 : dirSideways) {
                diagonal = moveOneSquare(forward, dir, dir2);
                legal = legalMove(board, diagonal, true, myColor);
                if (legal) {
                    legalMoves.add(new ChessMove(myPos, diagonal, null));
                }
            }
        }
        for(Direction dir : dirSideways){
            forward = moveOneSquare(myPos, dir, null);
            for(Direction dir2 : dirUpDown) {
                diagonal = moveOneSquare(forward, dir, dir2);
                legal = legalMove(board, diagonal, true, myColor);
                if (legal) {
                    legalMoves.add(new ChessMove(myPos, diagonal, null));
                }
            }
        }
        return legalMoves;
    }
}
