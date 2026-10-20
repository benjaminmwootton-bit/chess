package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess
{
    //return authToken
    String createUser(UserData userData) throws DataAccessException;
    String logInUser(UserData user);
    //returns UserData
    UserData getUserWithUsername(String username);
    String getUsernameWithAuthToken(String authToken);
    boolean userExists(String username);

    //return new authToken
    String createAuth();
    //return if authorization token exists
    boolean getAuth(String authToken);
    void deleteAuth(String authToken);
    boolean checkPassword(String username, String password);

    //returns gameID
    int createGame(String gameName);
    //returns gameID
    GameData getGame(int gameID);
    //returns all gameData
    ArrayList<GameData> listGames();
    boolean gameExists(int gameID);

    //to join a game/make a move
    void updateGame(GameData gameData) throws DataAccessException;
    void joinGame(int gameID, String username, ChessGame.TeamColor color);

    void clear();
}