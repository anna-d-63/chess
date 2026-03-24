package ui;

import model.GameData;

public interface ClientUI {
    String eval(String line);
    String firstLine();
    String help();
    boolean readyToBreak();
    String getAuthToken();
    GameData getGameData();
}
