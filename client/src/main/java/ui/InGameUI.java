package ui;

import client.ServerFacade;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.Objects;

public class InGameUI implements ClientUI {

    private final ServerFacade facade;
    public String authToken = null;
    public GameData gameData = null;

    InGameUI(int port) throws DataAccessException {
        facade = new ServerFacade(port);
    }

    @Override
    public String eval(String line) {
        return "";
    }

    @Override
    public String firstLine() {
        return "first line";
    }

    @Override
    public String help() {
        return "help menu";
    }

    @Override
    public boolean readyToBreak() {
        return false;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InGameUI inGameUI = (InGameUI) o;
        return Objects.equals(facade, inGameUI.facade) && Objects.equals(authToken, inGameUI.authToken) && Objects.equals(gameData, inGameUI.gameData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facade, authToken, gameData);
    }
}
