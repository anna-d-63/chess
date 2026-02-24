package server.requestAndResult;

public record LoginRequest(
        String username,
        String password
) {
}
