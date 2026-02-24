package server.requestAndResult;

public record RegisterRequest(
        String username,
        String password,
        String email) {
}
