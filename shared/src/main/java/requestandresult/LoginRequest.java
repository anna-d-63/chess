package requestandresult;

public record LoginRequest(
        String username,
        String password
) implements ParentRequest {

    @Override
    public boolean hasNullFields(){
        return username == null ||
                password == null;
    }
}
