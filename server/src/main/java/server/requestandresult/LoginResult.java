package server.requestandresult;

public record LoginResult(
        String username,
        String authToken
) {
}
