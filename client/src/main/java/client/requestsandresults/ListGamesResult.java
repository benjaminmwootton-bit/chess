package client.requestsandresults;

import model.GameData;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<GameData> games) {}