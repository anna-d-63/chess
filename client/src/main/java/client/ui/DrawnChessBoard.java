package client.ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static client.ui.EscapeSequences.*;

public class DrawnChessBoard {
    private final ChessBoard board;
    private final ChessGame.TeamColor perspective;

    //padded characters
    private final String[] columnHeaders = {
            " a" + "\u2003",
            " b" + "\u2003",
            " c" + "\u2003",
            " d" + "\u2003",
            " e" + "\u2003",
            " f" + "\u2003",
            " g" + "\u2003",
            " h" + "\u2003"
    };
    private final String[] rowHeaders = {
            " 1" + "\u2003",
            " 2" + "\u2003",
            " 3" + "\u2003",
            " 4" + "\u2003",
            " 5" + "\u2003",
            " 6" + "\u2003",
            " 7" + "\u2003",
            " 8" + "\u2003"
    };

    public DrawnChessBoard(ChessGame game, ChessGame.TeamColor perspective) {
        this.board = game.getBoard();
        this.perspective = perspective;
    }

    public static void main(String[] args) {
        ChessGame game1 = new ChessGame();
        DrawnChessBoard drawIt = new DrawnChessBoard(game1, WHITE);

        Collection<ChessMove> moves = game1.validMoves(new ChessPosition(7, 2));
        drawIt.createBoard(moves);
    }

    public void createBoard(Collection<ChessMove> legalMoves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeader(out);
        drawBulkBoard(out, legalMoves);
        drawHeader(out);
    }

    private void drawHeader(PrintStream out) {
        setGrey(out);
        out.print(EMPTY);
        if (perspective == BLACK) {
            for (int letter = 7; letter >= 0; letter--) {
                out.print(columnHeaders[letter]);
            }
        } else {
            for (int letter = 0; letter < 8; letter++) {
                out.print(columnHeaders[letter]);
            }
        }
        out.print(EMPTY);
        setBlack(out);
        out.println();
    }

    private void drawBulkBoard(PrintStream out, Collection<ChessMove> legalMoves) {
        ChessPosition startPosition = null;
        Collection<ChessPosition> endPositions = List.of();
        if (legalMoves != null && !legalMoves.isEmpty()) {
            startPosition = legalMoves.iterator().next().getStartPosition();
            endPositions = new ArrayList<>();
            for (ChessMove move : legalMoves) {
                endPositions.add(move.getEndPosition());
            }
        }

            for (int i = 0; i < 8; i++) {
                setGrey(out);
                if(perspective == BLACK) {out.print(rowHeaders[i]);}
                else {out.print(rowHeaders[7-i]);}

                for (int j = 7; j >= 0; j--) {
                    boolean even = (j % 2 == 1 && i % 2 == 0 || j % 2 == 0 && i % 2 == 1);
                    setSquareColor(out, even);
                    if (startPosition != null && !endPositions.isEmpty()){
                        if (startHere(startPosition, i, j)){
                            out.print(SET_BG_COLOR_BLUE);
                        } else if (potentialMove(endPositions, i, j)) {
                            setHighlightSquareColor(out, even);
                        }
                    }


                    printPiece(out, i, j);
                }
                setGrey(out);
                if(perspective == BLACK) {out.print(rowHeaders[i]);}
                else {out.print(rowHeaders[7-i]);}

                setBlack(out);
                out.println();
            }
    }

    private boolean startHere(ChessPosition startPosition, int i, int j){
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        if (perspective == BLACK){
            return i + 1 == row && j + 1 == col;
        } else {
            return 8 - i == row && 8 - j == col;
        }
    }

    private boolean potentialMove(Collection<ChessPosition> endPositions, int i, int j){
        for (ChessPosition potentialSpace : endPositions) {
            int row = potentialSpace.getRow();
            int col = potentialSpace.getColumn();
            if (perspective == BLACK) {
                if (i + 1 == row && j + 1 == col) {
                    return true;
                }
            } else {
                if (8 - i == row && 8 - j == col) {
                    return true;
                }
            }
        }
        return false;
    }

    private void printPiece(PrintStream out, int i, int j) {
        ChessPiece piece;
        if (perspective == BLACK) {
            piece = board.getPiece(new ChessPosition(i + 1, j + 1));
        } else {
            piece = board.getPiece(new ChessPosition(8-i, 8-j));
        }
        if (piece == null) {
            out.print(EMPTY);
            return;
        }

        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor pieceColor = piece.getTeamColor();

        if (pieceColor == WHITE) {out.print(SET_TEXT_COLOR_WHITE);}
        else {out.print(SET_TEXT_COLOR_BLACK);}

        if (type == ChessPiece.PieceType.PAWN) {out.print(BLACK_PAWN);}
        else if (type == ChessPiece.PieceType.ROOK) {out.print(BLACK_ROOK);}
        else if (type == ChessPiece.PieceType.KNIGHT) {out.print(BLACK_KNIGHT);}
        else if (type == ChessPiece.PieceType.BISHOP) {out.print(BLACK_BISHOP);}
        else if (type == ChessPiece.PieceType.QUEEN) {out.print(BLACK_QUEEN);}
        else if (type == ChessPiece.PieceType.KING) {out.print(BLACK_KING);}
    }

    private static void setGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_BOLD);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void setHighlightSquareColor(PrintStream out, boolean even) {
        if (even) {
            out.print(SET_BG_COLOR_GREEN);
        } else {
            out.print(SET_BG_COLOR_DARK_GREEN);
        }
    }

    private void setSquareColor(PrintStream out, boolean even) {
        if (even) {
            out.print(SET_BG_COLOR_TAN);
        } else {
            out.print(SET_BG_COLOR_BROWN);
        }
    }
}
