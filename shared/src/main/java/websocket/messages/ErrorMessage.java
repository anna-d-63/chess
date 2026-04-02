package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.ERROR;

public class ErrorMessage extends ServerMessage {
    public ErrorMessage(String errorMessage) {
        super(ERROR, errorMessage);
    }
}
