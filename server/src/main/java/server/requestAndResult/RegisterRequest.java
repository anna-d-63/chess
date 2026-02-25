package server.requestAndResult;

public record RegisterRequest (
        String username,
        String password,
        String email) implements ParentRequest {

    @Override
    public boolean hasNullFields(){
        return username == null ||
                password == null ||
                email == null;
    }
}
