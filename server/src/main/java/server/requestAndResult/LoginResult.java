package server.requestAndResult;

public record LoginResult(
        String username,
        String authToken
) {
}
