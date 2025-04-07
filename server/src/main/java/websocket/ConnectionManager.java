package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import model.data.ConnectionData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotifiResponse;
import websocket.messages.ServerMessage;


public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(ConnectionData connectionData, Session session ) {
        var connection = new Connection(connectionData, session);
        connections.put(connectionData.authToken(), connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void sendTo(Session session, Object data) throws IOException {
        session.getRemote().sendString(new Gson().toJson(data));
    }

    public void broadcast(String excludeToken, Integer gameID, ServerMessage.ServerMessageType notification, NotifiResponse message) throws IOException {

        if (!notification.equals(ServerMessage.ServerMessageType.NOTIFICATION)) {
            return;
        }

        var removeList = new ArrayList<Connection>();

        for (var c: connections.values()) {
            if (c.session.isOpen()){
                if (!c.connectionData.authToken().equals(excludeToken) && Objects.equals(c.connectionData.gameID(), gameID)){
                    c.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c: removeList) {
            connections.remove(c.connectionData.authToken());
        }
    }
}
