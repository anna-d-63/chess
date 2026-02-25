package server.requestAndResult;

public record LogoutRequest(
        String authToken
) implements ParentRequest{

    @Override
    public boolean hasNullFields(){
        return authToken == null;
    }
}
