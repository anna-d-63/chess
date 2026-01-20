These are my notes! I am making them so that I can demonstate git use.


piece moves.

need to know where your pieces are and where other pieces are. \
reusable functionality? what can be reused \
front, back, diagonal, etc. \
don't make a subclass for each piece \
piece move calculator in a separate class, parent class \
then subclass for each piece \

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


//private static final Map<Character, ChessPiece.PieceType> CHAR_TO_TYPE_MAP = Map.of(
//            'p', ChessPiece.PieceType.PAWN,
//            'n', ChessPiece.PieceType.KNIGHT,
//            'r', ChessPiece.PieceType.ROOK,
//            'q', ChessPiece.PieceType.QUEEN,
//            'k', ChessPiece.PieceType.KING,
//            'b', ChessPiece.PieceType.BISHOP);
//
//    public static ChessBoard loadBoard(String boardText) {
//        var board = new ChessBoard();
//        int row = 8;
//        int column = 1;
//        for (var c : boardText.toCharArray()) {
//            switch (c) {
//                case '\n' -> {
//                    column = 1;
//                    row--;
//                }
//                case ' ' -> column++;
//                case '|' -> {
//                }
//                default -> {
//                    ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK
//                            : ChessGame.TeamColor.WHITE;
//                    var type = CHAR_TO_TYPE_MAP.get(Character.toLowerCase(c));
//                    var position = new ChessPosition(row, column);
//                    var piece = new ChessPiece(color, type);
//                    board.addPiece(position, piece);
//                    column++;
//                }
//            }
//        }
//        return board;
//    }