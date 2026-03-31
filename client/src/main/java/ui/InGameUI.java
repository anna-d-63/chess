package ui;

import chess.ChessGame;
import client.ServerFacade;
import exceptions.DataAccessException;
import model.GameData;
import requestandresult.LogoutRequest;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class InGameUI implements ClientUI {

    private final ServerFacade facade;
    public String authToken = null;
    public GameData gameData = null;
    public ChessGame.TeamColor color = null;

    InGameUI(int port) {
        facade = new ServerFacade(port);
    }

    @Override
    public String eval(String line) {
        try {
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "redraw" -> redrawBoard();
                case "menu" -> backToGameMenu();
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
        if (color == ChessGame.TeamColor.BLACK) {
            chessGame.setTeamTurn(ChessGame.TeamColor.BLACK);
        } else {
            chessGame.setTeamTurn(ChessGame.TeamColor.WHITE);
        }
        DrawnChessBoard drawBoard = new DrawnChessBoard(chessGame);
        drawBoard.createBoard(null);
        return RESET_BG_COLOR;
    }

    @Override
    public String help() {
        return SET_TEXT_COLOR_BLUE + "redraw " +
                SET_TEXT_COLOR_LIGHT_GREY + "- redraw chessboard \n" +
                SET_TEXT_COLOR_BLUE + "menu " +
                SET_TEXT_COLOR_LIGHT_GREY + "- back to game menu \n" +
                SET_TEXT_COLOR_BLUE + "quit " +
                SET_TEXT_COLOR_LIGHT_GREY + "- leave the application \n" +
                SET_TEXT_COLOR_BLUE + "help " +
                SET_TEXT_COLOR_LIGHT_GREY + "- view this menu again \n" +
                SET_TEXT_COLOR_BLUE + "highlight " +
                SET_TEXT_COLOR_LIGHT_GREY + "- select a square and see that piece's legal moves";
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
