package ui;

import client.ServerFacade;
import dataaccess.DataAccessException;

public class PreLoginUI implements ClientUI {

    private final ServerFacade server;

    PreLoginUI(int port) throws DataAccessException {
        server = new ServerFacade(port);
    }

    @Override
    public String eval(String line) {
        return "";
    }

    @Override
    public String firstLine() {
        return "";
    }

    @Override
    public String help() {
        return "";
    }
}
