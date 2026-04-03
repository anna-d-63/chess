package client.ui;

import chess.ChessGame;
import client.websocket.ServerMessageObserver;
import client.websocket.WebsocketCommunicator;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import static client.ui.EscapeSequences.*;


public class Repl implements ServerMessageObserver {
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private final InGameUI inGame;

    public Repl(int port) throws DataAccessException {
        WebsocketCommunicator ws = new WebsocketCommunicator(String.format("http://localhost:%d", port), this);
        preLogin = new PreLoginUI(port);
        postLogin = new PostLoginUI(port, ws);
        inGame = new InGameUI(port, ws);
    }

    public void run() {
        ClientUI ui = preLogin;
        System.out.println(ui.firstLine());
        System.out.print(ui.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();

            String line = scanner.nextLine();
            try {
                result = ui.eval(line);
                System.out.println(SET_TEXT_COLOR_LIGHT_GREY + result);
                ClientUI newUI = switchUI(ui);
                if (!newUI.equals(ui)) {
                    System.out.println();
                    System.out.println(newUI.firstLine());
                    System.out.print(newUI.help());
                }
                ui = newUI;
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_GREEN + ">>> ");
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> displayNotification( (NotificationMessage) serverMessage);
            case LOAD_GAME -> loadGame( (LoadGameMessage) serverMessage);
            case ERROR -> displayError( (ErrorMessage) serverMessage);
        }
    }

    private void displayNotification(NotificationMessage message) {
        System.out.println(RESET_BG_COLOR + SET_TEXT_COLOR_MAGENTA + message.getMessage());
    }

    private void loadGame(LoadGameMessage message) {
        String gameJson = message.getGame();
        ChessGame.TeamColor color = message.getColor();
        if (color == null) {
            color = ChessGame.TeamColor.WHITE;
        }
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        System.out.println();
        new DrawnChessBoard(game, color).createBoard(null);
    }

    private void displayError(ErrorMessage message) {
        System.out.println(RESET_BG_COLOR + SET_TEXT_COLOR_RED + message);
    }

    private ClientUI switchUI (ClientUI currentUI) {
        if (currentUI.getAuthToken() == null) {
            return preLogin;
        } else if (currentUI.getAuthToken() != null &&
                currentUI.getGameData() == null) {
            postLogin.setAuthToken(currentUI.getAuthToken());
            return postLogin;
        } else {
            inGame.setAuthToken(currentUI.getAuthToken());
            inGame.setGameData(currentUI.getGameData());
            inGame.setColor(currentUI.getColor());
            return inGame;
        }
    }

}