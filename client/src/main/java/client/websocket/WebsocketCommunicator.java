package client.websocket;

import exceptions.DataAccessException;
import jakarta.websocket.*;

import java.net.URI;

public class WebsocketCommunicator extends Endpoint {

    //SENDS AND RECEIVES WEBSOCKET COMMUNICATIONS

    Session session;

    public WebsocketCommunicator (String url) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //message handler?

        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
