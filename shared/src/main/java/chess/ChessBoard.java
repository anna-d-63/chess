package chess;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 * <p>
 * stores all uncaptured pieces in a game
 */
public class ChessBoard implements Cloneable{

    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor[] colors = {ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK};
        ChessPiece.PieceType[] pieces = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                                        ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                                        ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK,ChessPiece.PieceType.PAWN};
        // white first row
        for(int j = 0; j < 8; j++){
            squares[0][j] = new ChessPiece(colors[0], pieces[j]);
            squares[1][j] = new ChessPiece(colors[0], pieces[8]);
            squares[6][j] = new ChessPiece(colors[1], pieces[8]);
            squares[7][j] = new ChessPiece(colors[1], pieces[j]);
        }

        //empty middle
        for(int i = 2; i < 6; i++){
            for(int j = 0; j < 8; j++){
                squares[i][j] = null;
            }
        }
    }

    @Override
    protected ChessBoard clone(){
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            clone.squares = Arrays.copyOf(squares, squares.length);
            for(int i = 0; i <8; i++){
                clone.squares[i] = Arrays.copyOf(squares[i], squares[i].length);
            }

            return clone;
        } catch (CloneNotSupportedException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        for(int i = 0; i < 8; i++){
            board.append("|");
            for(int j = 0; j < 8; j++){
                if(squares[i][j] != null) {
                    board.append(squares[i][j].toString());
                    board.append("|");
                } else {
                    board.append(" ");
                    board.append("|");
                }
            }
            board.append('\n');
        }
        return board.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

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
}
