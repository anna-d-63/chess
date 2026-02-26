package server.requestandresult;

public record RegisterResult(
        String username,
        String authToken
) {
}
