package service.requestsandresults;

import java.util.ArrayList;
import model.GameData;

public record ListGamesResult(ArrayList<GameData> games) {}