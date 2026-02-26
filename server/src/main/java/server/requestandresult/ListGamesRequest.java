package server.requestandresult;

public record ListGamesRequest(
        String authToken
) implements ParentRequest {
    @Override
    public boolean hasNullFields(){
        return authToken == null;
    }
}
