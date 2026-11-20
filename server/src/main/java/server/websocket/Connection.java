package server.websocket;

import chess.ChessGame;
import jakarta.websocket.Session;

public record Connection(String username, Session session, Integer gameID, ChessGame.TeamColor teamColor){}