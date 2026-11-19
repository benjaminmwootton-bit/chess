package server.websocket;

import java.util.*;

public class ConnectionManager {

    private final Map<Integer, Set<Connection>> connections = new HashMap<>();

    public void addConnection(Integer gameID, Connection connection){
        if(connections.get(gameID) == null){
            HashSet<Connection> cons = new HashSet<>();
            cons.add(connection);
            connections.put(gameID, cons);
        }
        else{
            connections.get(gameID).add(connection);
        }
    }

    public void removeConnection(Integer gameID, Connection connection){
        Set<Connection> cons = connections.get(gameID);
        if(cons != null) {
            connections.get(gameID).remove(connection);
            if(cons.isEmpty()){
                connections.remove(gameID);
            }
        }
    }

    public Set<Connection> getConnections(Integer gameID) {
        return connections.get(gameID);
    }
}