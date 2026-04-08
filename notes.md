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


private static final Map<Character, ChessPiece.PieceType> CHAR_TO_TYPE_MAP = Map.of(
'p', ChessPiece.PieceType.PAWN,
'n', ChessPiece.PieceType.KNIGHT,
'r', ChessPiece.PieceType.ROOK,
'q', ChessPiece.PieceType.QUEEN,
'k', ChessPiece.PieceType.KING,
'b', ChessPiece.PieceType.BISHOP);

    public static ChessBoard loadBoard(String boardText) {
        var board = new ChessBoard();
        int row = 8;
        int column = 1;
        for (var c : boardText.toCharArray()) {
            switch (c) {
                case '\n' -> {
                    column = 1;
                    row--;
                }
                case ' ' -> column++;
                case '|' -> {
                }
                default -> {
                    ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;
                    var type = CHAR_TO_TYPE_MAP.get(Character.toLowerCase(c));
                    var position = new ChessPosition(row, column);
                    var piece = new ChessPiece(color, type);
                    board.addPiece(position, piece);
                    column++;
                }
            }
        }
        return board;
    }




Phase 1

Chess game

pare list of valid moves down to what you can actually do.\
If it's not your turn, no valid moves\
Need to be able to identify when you're in check, and pare down the list of valid moves to what you can do to get you out of check\
checkmate is the given team has no way to protect their king from being captured\
stalemate is if the given team has no legal moves but their king is not in immediate danger


how do you know if you're in check?\
You have to look at all of your opponent's moves, if any of those end positions are where your king is then you're in check.\
you will have to iterate throughout the whole board.\
valid moves are moves that don't put you in check, or if you are in check only moves that get you out of check

valid moves is piece moves, start there. check each move to see if you do it, will you be in check.

or make the move. basically just try the move and see if it leaves you in check.



make a copy of the board, run all the simulations on that



## Phase 5

display menus/accept and process input (REPL) 

need to make a list of options of what the user can do. For example, 
1. Login 
2. Register 
3. Help
4. Quit

need prelogin UI and postlogin UI. keep track of the authtoken in loop to see if they are logged in. \
If authtoken is null, not logged in. If not null, they are logged in.

redo game ID thing. they should be sequential and autoincremented. \
independent of what the game id is, they should be listed in sequential order. \
need to keep a mapping of display number and actual game ID.

Draw Chess Board \
can use unicode chess characters.
if you observe, draw from the point of view of the white player. \
difference in where the numbers and letters are. \
Invoke Server API Endpoints \
Write tests

## Phase 6

maybe put up list of available games in first line for post login ui \
when the second person joins the game, the first person (and observer if applicable) \
gets notification. \
when the observer joins, everyone else gets a notification. \
when a move is made, their board updates and everyone else gets a notification \
and the new board is redrawn. \
Anyone can highlight legal moves for any piece. 

resign means I give up. \
leave game means that your position is available and someone else can login \
as that player. It could mean resign but do the first thing

Make move \
need to see if in check, checkmate, stalemate \
if those happen, notify everyone. If checkmate or stalemate, game is over. \
that needs to happen in server

when you make a move, you are reading the game, deserializing the game, change the piece \
then reserialize the board and send it back

for highlighting, you want to have three different colors: which piece are you checking \
dark highlight, light highlight.

maybe start draw the chess board differently. with highlighting. do resign and leave then make move. 

maybe leave game is a join as a null user. maybe that doesn't work. I don't have update game. \
maybe its a websocket notification. need a flag so you can't make a move after the game is over. 

need to know who is in each game. have a map that is keyed by game id and list of people in the game.

this phase takes the longest, get started SOON. you are not supposed to add another web api

            //make sure it isn't null before this line
//            if (gameData.game().getBoard().getPiece(startPosition).getPieceType() == PAWN &&
//                    (startPosition.getRow() == 8 || startPosition.getRow() == 1) &&
//                params[0].charAt(6) != ':' && params[0].length() < 11){
//                throw new DataAccessException("Must include promotion piece");
//            }