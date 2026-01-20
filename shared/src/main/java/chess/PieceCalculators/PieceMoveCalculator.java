package chess.PieceCalculators;

import chess.*;

import java.util.Collection;
import java.util.List;

public class PieceMoveCalculator {
    public PieceMoveCalculator(){}

    public enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

//    public static boolean moveStraightOne(ChessBoard board, ChessPosition myPosition, Direction dir){
//        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
//        int row = myPosition.getRow();
//        int col = myPosition.getColumn();
//        if(dir == Direction.NORTH){row++;}
//        else if(dir == Direction.EAST){col++;}
//        else if(dir == Direction.SOUTH){row--;}
//        else {col--;}
//
//        if(row > 8 || row < 1 || col > 8 || col < 1){return false;}
//
//        ChessPosition newPos = new ChessPosition(row, col);
//        ChessPiece incomingPiece = board.getPiece(newPos);
//
//        if(incomingPiece == null){return true;}
//        else return incomingPiece.getTeamColor() != myColor;
//    }
//
//    public static boolean moveDiagonalOne(ChessBoard board, ChessPosition myPosition, Direction dir1, Direction dir2){
//        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
//        int row = myPosition.getRow();
//        int col = myPosition.getColumn();
//        if(dir1 == Direction.NORTH || dir2 == Direction.NORTH){row++;}
//        if(dir1 == Direction.EAST || dir2 == Direction.EAST){col++;}
//        if(dir1 == Direction.SOUTH || dir2 == Direction.SOUTH){row--;}
//        if(dir1 == Direction.WEST || dir2 == Direction.WEST){col--;}
//
//        if(row > 8 || row < 1 || col > 8 || col < 1){return false;}
//
//        ChessPosition newPos = new ChessPosition(row, col);
//        ChessPiece incomingPiece = board.getPiece(newPos);
//
//        if(incomingPiece == null){return true;}
//        else return incomingPiece.getTeamColor() != myColor;
//    }

    public static ChessPosition moveOneSquare(ChessPosition myPosition, Direction dir1, Direction dir2){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if(dir1 == Direction.NORTH || dir2 == Direction.NORTH){row++;}
        if(dir1 == Direction.EAST || dir2 == Direction.EAST){col++;}
        if(dir1 == Direction.SOUTH || dir2 == Direction.SOUTH){row--;}
        if(dir1 == Direction.WEST || dir2 == Direction.WEST){col--;}

        return new ChessPosition(row, col);
    }

    public static boolean legalMove(ChessBoard board, ChessPosition myPos, ChessPosition newPos, boolean canCapture, ChessGame.TeamColor myColor){
        int row = newPos.getRow();
        int col = newPos.getColumn();

        if(row > 8 || row < 1 || col > 8 || col < 1){return false;} //can't move out of bounds

        ChessPiece incomingPiece = board.getPiece(newPos);
        if(incomingPiece != null){
            if(canCapture){
                return incomingPiece.getTeamColor() != myColor; //it's a legal move to capture the enemy color
            } else {return false;} //mostly for a pawn because they can't capture by going forward
        } else {return true;} //can move there if the space is empty
    }

    public static boolean continueOn(ChessBoard board, ChessPosition newPos){
        int row = newPos.getRow();
        int col = newPos.getColumn();

        if(row > 8 || row < 1 || col > 8 || col < 1){return false;}
        return board.getPiece(newPos) == null;
    }
    /*
    right now my problem is that if it captures a piece, it doesn't know to end.
    capturing a piece is a legal move but it doesn't realize that it shouldn't go any further.

    maybe a captured piece flag? then legal move would have to return multiple values and I think that's bad code. but maybe its not.
     */
    /*
    Maybe I want more general piece moves in here, move forward, move diagonal, move sideways

    rook
    straight, have four modes: North, South, East, West
    +1/-1 check if a piece is there, if not, add it to the valid moves
        if there is a piece there, and it is your color, do not add it and break
        if there is a piece there, and it is NOT your color, add it and break
        if you reach the end of the board, break

     bishop
     diagonal, have four modes: NE, SE, SW, NW
        +1/-1 check if a piece is there, if not, add it to the valid moves
        if there is a piece there, and it is your color, do not add it and break
        if there is a piece there, and it is NOT your color, add it and break
        if you reach the end of the board, break

     knight
     one straight, one diagonal in each direction, check if there is a piece there, if not, add to valid moves
     if there is a piece there, and it is your color, do not add it to the valid moves
     if there is a piece there, and it is NOT your color, add it
     if it's off the board, don't add it

     King
     move one in each direction

     Queen
     do bishop and rook

     pawn
     move one straight, must be in your direction



     what I can decompose from this
     moveStraightOne(board, position, direction)
        if I move forward one in that position, is it legal?
        if so return true
        else return false

     moveDiagonalOne(board, position, direction1, direction2)
        if I move diagonal one in that position, is it legal?
        if so return true,
        else return false

     enum of directions, NORTH, EAST, SOUTH WEST

     rook
     NORTH
     moveStraightOne(board, position, NORTH) -> initial position
     if that's true, then run moveStraightOne again with updated position. do that until you can't move north anymore
     do the same for EAST, SOUTH, WEST, each time starting at initial position and going until you can't go anymore

     bishop
     four cases NE, SE, SW, NW
     moveDiagonalOne(board, position, NORTH, EAST) -> initial position
     do that until you can't
     do the same for SE, SW, NW

     knight
     8 things to try straight then diagonal
     NORTH NW, NORTH NE, EAST NE, EAST SE, SOUTH SE, SOUTH SW, WEST SW, WEST NW
     always from start positon
     add all the valid ones to the list

     king
     try moveOneStraight in all directions
     try moveOneDiagonal in all directions
     add all the valid ones to the list

     queen
     lowkey maybe just run rook and bishop functions and take all the valid ones from there

     pawn
     know what color you are
     if white, move SOUTH always
        check moveOneStraight SOUTH, add it to the moves list
        if that was true, check moveOneStraight SOUTH again, and add it to the list if valid
        like move one diagonal, but going to need to be more specific, so probably just write it out
        check if there is an enemy piece immediately SE or SW, if so, add it to the list of valid moves
     if black, move NORTH always
        same thing but NORTH






     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {return new BishopMoveCalculator().pieceMoves(board, myPosition);}
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {return new RookMoveCalculator().pieceMoves(board, myPosition);}
        else{return List.of();}
    }
}
