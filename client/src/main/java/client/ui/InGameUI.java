package client.ui;

import chess.ChessGame;
import chess.ChessPosition;
import client.ServerFacade;
import client.websocket.ServerMessageObserver;
import client.websocket.WebsocketCommunicator;
import exceptions.DataAccessException;
import model.GameData;
import requestandresult.LogoutRequest;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Objects;

import static client.ui.EscapeSequences.*;
import static client.ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class InGameUI implements ClientUI {

    private final ServerFacade facade;
    WebsocketCommunicator ws;
    public String authToken = null;
    public GameData gameData = null;
    public ChessGame.TeamColor color = null;

    InGameUI(int port, WebsocketCommunicator ws) {
        facade = new ServerFacade(port);
        this.ws = ws;
    }

    @Override
    public String eval(String line) {
        try {
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "redraw" -> redrawBoard();
                case "highlight" -> highlightMoves(params);
                case "leave" -> backToGameMenu();
                case "quit" -> quitAndLogout();
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String redrawBoard() {
        return firstLine();
    }

    private final Character[] cols = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

    private String highlightMoves(String[] params) throws DataAccessException {
        if (params.length == 1) {
            ChessPosition square = getSquare(params[0]);
            ChessGame game = gameData.game();
            DrawnChessBoard drawBoard = new DrawnChessBoard(game, color);
            drawBoard.createBoard(game.validMoves(square));
            return RESET_BG_COLOR;
        }
        throw new DataAccessException("<SQUARE> must be in the form a1");
    }

    private ChessPosition getSquare(String square) throws DataAccessException {
        try {
            char colChar = square.charAt(0);
            char rowChar = square.charAt(1);

            int col = Arrays.asList(cols).indexOf(colChar);
            int row = Character.getNumericValue(rowChar);

            if (col < 0 || col > 7 || row < 0 || row > 8) {
                throw new Exception();
            }

            return new ChessPosition(row, col + 1);
        } catch (Exception e) {
            throw new DataAccessException("<SQUARE> must be in the form of a1");
        }
    }

    private String backToGameMenu() {
        gameData = null;
        return "";
    }

    private String quitAndLogout() throws DataAccessException {
        var logoutRequest = new LogoutRequest(authToken);
        facade.logout(logoutRequest);
        return "quit";
    }

    @Override
    public String firstLine() {
        ChessGame chessGame = gameData.game();
        DrawnChessBoard drawBoard = new DrawnChessBoard(chessGame, color);
        drawBoard.createBoard(null);
        return RESET_BG_COLOR;
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "redraw " +
                SET_TEXT_COLOR_LIGHT_GREY + "- redraw chessboard \n" +
                SET_TEXT_COLOR_BLUE + "highlight <SQUARE>" +
                SET_TEXT_COLOR_LIGHT_GREY + "- select a square (in the form of a1) and see that piece's legal moves \n" +
                SET_TEXT_COLOR_BLUE + "menu " +
                SET_TEXT_COLOR_LIGHT_GREY + "- back to game menu \n" +
                SET_TEXT_COLOR_BLUE + "quit " +
                SET_TEXT_COLOR_LIGHT_GREY + "- leave the application \n" +
                SET_TEXT_COLOR_BLUE + "help " +
                SET_TEXT_COLOR_LIGHT_GREY + "- view this menu again";
        /*
        help - help text
        redraw chess board
        leave - Removes the user from the game (whether they are playing or observing the game).
            The client transitions back to the Post-Login UI.
        Make move - Allow the user to input what move they want to make.
                The board is updated to reflect the result of the move,
                and the board automatically updates on all clients involved in the game.
        Resign - Prompts the user to confirm they want to resign.
                If they do, the user forfeits the game and the game is over.
                Does not cause the user to leave the game.
        Highlight legal moves - Allows the user to input the piece for which they want to highlight legal moves.
                                The selected piece’s current square and all squares it can legally move to are highlighted.
                                This is a local operation and has no effect on remote users’ screens.
         */
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public GameData getGameData() {
        return this.gameData;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public void setColor(ChessGame.TeamColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InGameUI inGameUI = (InGameUI) o;
        return Objects.equals(facade, inGameUI.facade) && Objects.equals(authToken, inGameUI.authToken)
                && Objects.equals(gameData, inGameUI.gameData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facade, authToken, gameData);
    }
}
