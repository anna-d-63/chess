package chess.piececalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator extends PieceMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPos) {
        Collection<ChessMove> legalMoves = new ArrayList<>();

        ChessGame.TeamColor myColor = board.getPiece(myPos).getTeamColor();
        Direction dir;
        Direction[] diagonalDir = {Direction.EAST, Direction.WEST};
        ChessPiece.PieceType[] promoPieces = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP,
                                                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};
        boolean legal;
        boolean legal2;

        if(myColor == ChessGame.TeamColor.BLACK){
            dir = Direction.SOUTH;
        } else {
            dir = Direction.NORTH;
        }

        //move forward logic

        ChessPosition oneSquare = moveOneSquare(myPos, dir, null);
        legal = legalMove(board, oneSquare, false, myColor);
        if(legal){
            if(oneSquare.getRow() == 1 || oneSquare.getRow() == 8){         //if it makes it to the opposite side of the board,
                for(ChessPiece.PieceType promoPiece : promoPieces){         //promote the pawn to a queen
                    legalMoves.add(new ChessMove(myPos, oneSquare, promoPiece));
                }
            } else{
                legalMoves.add(new ChessMove(myPos, oneSquare, null));
            }

//                                                                                          //if you are on your color's starting row and its already legal to move one
            if(myPos.getRow() == 7 && myColor == ChessGame.TeamColor.BLACK ||
                myPos.getRow() == 2 && myColor == ChessGame.TeamColor.WHITE) {              //check if it is legal to move 2
                ChessPosition secondSquare = moveOneSquare(oneSquare, dir, null);
                legal2 = legalMove(board, secondSquare, false, myColor);
                if (legal2) {
                    legalMoves.add(new ChessMove(myPos, secondSquare, null));
                }
            }
        }

        //capture on diagonal logic
        boolean captures;
        boolean isEmpty;
        for(Direction dir2 : diagonalDir){
            ChessPosition captureSquare = moveOneSquare(myPos, dir, dir2);
            captures = legalMove(board, captureSquare, true, myColor);
            isEmpty = continueOn(board, captureSquare);
            if(captures && !isEmpty){
                if(captureSquare.getRow() == 1 || captureSquare.getRow() == 8){         //if it makes it to the opposite side of the board,
                    for(ChessPiece.PieceType promoPiece : promoPieces){                 //promote the pawn to a queen
                        legalMoves.add(new ChessMove(myPos, captureSquare, promoPiece));
                    }
                } else {
                    legalMoves.add(new ChessMove(myPos, captureSquare, null));
                }
            }
        }

        return legalMoves;
    }
}
