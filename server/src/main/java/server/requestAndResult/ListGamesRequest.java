package server.requestAndResult;

public record ListGamesRequest(
        String authToken
) implements ParentRequest {
    @Override
    public boolean hasNullFields(){
        return authToken == null;
    }
}
