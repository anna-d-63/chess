package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator extends PieceMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();

        boolean legal;
        boolean canContinue;
        ChessPosition myPos;

        for(Direction dir : Direction.values()) {
            canContinue = true;
            myPos = myPosition;
            while (canContinue) {
                ChessPosition newPos = moveOneSquare(myPos, dir, null);
                legal = legalMove(board, myPos, newPos, true, myColor);
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
}
