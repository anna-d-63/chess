package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawnChessBoard {
    private final ChessBoard board;
    private final ChessGame.TeamColor teamColor;

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

    public DrawnChessBoard(ChessGame game) {
        this.board = game.getBoard();
        this.teamColor = game.getTeamTurn();
    }

    public static void main(String[] args) {
        ChessGame game1 = new ChessGame();
        DrawnChessBoard drawIt = new DrawnChessBoard(game1);

        drawIt.createBoard();
    }

    public void createBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeader(out);
        drawBulkBoard(out);
        drawHeader(out);
    }

    private void drawHeader(PrintStream out) {
        setGrey(out);
        out.print(EMPTY);
        if (teamColor == ChessGame.TeamColor.BLACK) {
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

    private void drawBulkBoard(PrintStream out) {
            for (int i = 0; i < 8; i++) {
                setGrey(out);
                if(teamColor == ChessGame.TeamColor.BLACK) {out.print(rowHeaders[i]);}
                else {out.print(rowHeaders[7-i]);}

                for (int j = 0; j < 8; j++) {
                    if (j % 2 == 0 && i % 2 == 0 ||
                            j % 2 == 1 && i % 2 == 1) {
                        out.print(SET_BG_COLOR_TAN);
                    } else {
                        out.print(SET_BG_COLOR_BROWN);
                    }
                    printPiece(out, i, j);
                }
                setGrey(out);
                if(teamColor == ChessGame.TeamColor.BLACK) {out.print(rowHeaders[i]);}
                else {out.print(rowHeaders[7-i]);}

                setBlack(out);
                out.println();
            }
    }

    private void printPiece(PrintStream out, int i, int j) {
        ChessPiece piece;
        if (teamColor == ChessGame.TeamColor.BLACK) {
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

        if (pieceColor == ChessGame.TeamColor.WHITE) {out.print(SET_TEXT_COLOR_WHITE);}
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
}
