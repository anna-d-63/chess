package server.requestandresult;

public record JoinGameRequest(
        String authToken,
        String playerColor,
        int gameID
) implements ParentRequest {

    @Override
    public boolean hasNullFields() {
        return authToken == null ||
                playerColor == null ||
                gameID == 0;
    }
}
