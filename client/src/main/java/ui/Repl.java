package ui;

import dataaccess.DataAccessException;

import java.util.Scanner;
import static ui.EscapeSequences.*;


public class Repl {
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private final InGameUI inGame;

    public Repl(int port) throws DataAccessException {
        preLogin = new PreLoginUI(port);
        postLogin = new PostLoginUI(port);
        inGame = new InGameUI(port);
    }

    public void loop(ClientUI ui) {
        System.out.println(ui.firstLine());
        System.out.print(ui.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();

            String line = scanner.nextLine();
            try {
                result = ui.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_BLACK + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}
