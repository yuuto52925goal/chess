package websocket;

import model.data.ConnectionData;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {

    public Session session;
    public ConnectionData connectionData;

    public Connection(ConnectionData connectionData, Session session ) {
        this.connectionData = connectionData;
        this.session = session;
    }

    void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
