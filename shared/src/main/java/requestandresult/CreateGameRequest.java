package requestandresult;

public record CreateGameRequest(
        String authToken,
        String gameName
) implements ParentRequest {
    @Override
    public boolean hasNullFields(){
        return authToken == null ||
                gameName == null;
    }
}
