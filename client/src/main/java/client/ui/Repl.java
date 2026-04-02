package client.ui;

import client.websocket.ServerMessageObserver;
import client.websocket.WebsocketCommunicator;
import exceptions.DataAccessException;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import static client.ui.EscapeSequences.*;


public class Repl implements ServerMessageObserver {
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private final InGameUI inGame;
    private final WebsocketCommunicator ws;

    public Repl(int port) throws DataAccessException {
        ws = new WebsocketCommunicator(String.format("http://localhost:%d", port), this);
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
    public void notify(ServerMessage message) {
        System.out.println(message.getMessage());
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