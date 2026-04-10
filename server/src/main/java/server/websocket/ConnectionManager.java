package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet())
                .add(session);
        connections.get(gameID).add(session);
    }

    public void remove(Session session, Integer gameID) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    public void broadcast(Session excludeSession, ServerMessage notification, Integer gameID) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void selfBroadcast(Session onlySession, ServerMessage notification, Integer gameID) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (c.equals(onlySession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
