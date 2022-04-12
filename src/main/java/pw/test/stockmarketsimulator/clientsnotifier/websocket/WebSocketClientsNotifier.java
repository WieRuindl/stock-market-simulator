package pw.test.stockmarketsimulator.clientsnotifier.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pw.test.stockmarketsimulator.clientsnotifier.ClientsNotifier;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@ServerEndpoint(value = "/tradeledger")
public class WebSocketClientsNotifier implements ClientsNotifier {

    private final static Set<WebSocketClient> WEB_SOCKET_CLIENTS = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        WebSocketClient client = new WebSocketClient(session);
        WEB_SOCKET_CLIENTS.add(client);
        log.info(String.format("New session %s connected! Connected clients: %s", session.getId(), WEB_SOCKET_CLIENTS.size()));
    }

    @OnClose
    public void onClose(Session session) {
        Optional<WebSocketClient> client = WEB_SOCKET_CLIENTS.stream()
                .filter(c -> c.getSession().equals(session))
                .findFirst();
        if (client.isPresent()) {
            WEB_SOCKET_CLIENTS.remove(client.get());
            log.info(String.format("Session %s disconnected. Total connected clients: %s", session.getId(), WEB_SOCKET_CLIENTS.size()));
        }
    }

    @Override
    public void notifyClients(String message) {
        for (WebSocketClient webSocketClient : WEB_SOCKET_CLIENTS) {
            webSocketClient.sendMessage("New Trade: " + message);
        }
    }
}
