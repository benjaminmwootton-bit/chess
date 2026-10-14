package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess
{
    //return authToken
    String createUser(UserData userData) throws DataAccessException;
    //returns UserData
    UserData getUser(String username);
    boolean userExists(String username);

    //return new authToken
    String createAuth();
    //return if authorization token exists
    boolean getAuth(String authToken);
    void deleteAuth(String authToken);

    //returns gameID
    int createGame(String gameName);
    //returns gameID
    GameData getGame(int gameID);
    //returns all gameData
    List<GameData> listGames();

    //to join a game/make a move
    void updateGame(GameData gameData) throws DataAccessException;

    void clear();
}
