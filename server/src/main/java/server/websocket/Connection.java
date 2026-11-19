package server.websocket;

import jakarta.websocket.Session;

public record Connection(String username, Session session, Integer gameID){}