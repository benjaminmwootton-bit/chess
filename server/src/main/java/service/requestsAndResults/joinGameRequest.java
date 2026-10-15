package service.requestsAndResults;

import chess.ChessGame;

public record joinGameRequest(String authToken, ChessGame.TeamColor color, int gameID) {}