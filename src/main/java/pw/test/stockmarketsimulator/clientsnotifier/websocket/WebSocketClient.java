package pw.test.stockmarketsimulator.clientsnotifier.websocket;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;

@Slf4j
@Data
public class WebSocketClient {

    private final Session session;

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("Caught exception while sending message to Session Id: " + session.getId(), e.getMessage(), e);
        }
    }
}
