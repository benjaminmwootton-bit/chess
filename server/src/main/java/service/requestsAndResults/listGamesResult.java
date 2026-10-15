package service.requestsAndResults;

import java.util.ArrayList;
import model.GameData;

public record listGamesResult(ArrayList<GameData> games) {}