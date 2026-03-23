package ui;

import client.ServerFacade;
import dataaccess.DataAccessException;

public class InGameUI implements ClientUI {

    private final ServerFacade server;

    InGameUI(int port) throws DataAccessException {
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

    @Override
    public boolean readyToBreak() {
        return false;
    }
}
