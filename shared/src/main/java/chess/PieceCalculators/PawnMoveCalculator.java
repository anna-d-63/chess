package chess.PieceCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator extends PieceMoveCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> legalMoves = new ArrayList<>();

        ChessGame.TeamColor myColor = board.getPiece(myPos).getTeamColor();
        Direction dir;
        boolean legal;

        if(myColor== ChessGame.TeamColor.BLACK){
            dir = Direction.NORTH;
        } else {
            dir = Direction.SOUTH;
        }

        ChessPosition oneSquare = moveOneSquare(myPos, dir, null);
        legal = legalMove(board, oneSquare, false, myColor);
        if(legal){
            //TODO: FIGURE OUT PROMOTION PIECE LOGIC
            legalMoves.add(new ChessMove(myPos, oneSquare, null));
        }

        //TODO: 2 FORWARD LOGIC


        //TODO: CAPTURING LOGIC

        return legalMoves;
    }
}
