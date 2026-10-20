package Service.requestsAndResults;

import java.util.ArrayList;
import model.GameData;

public record ListGamesResult(ArrayList<GameData> games) {}