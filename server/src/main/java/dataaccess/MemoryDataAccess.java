package dataaccess;

import java.util.*;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import model.GameData;

public class MemoryDataAccess implements DataAccess
{
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameCount = 0;

    public String createUser(UserData user) throws DataAccessException
    {
        if(users.containsKey(user.username()))
            throw new DataAccessException("Username already taken");
        users.put(user.username(), user);
        String authToken = createAuth();
        authTokens.put(authToken, new AuthData(authToken, user.username()));
        return authToken;
    }
    public String logInUser(UserData user)
    {
        String authToken = createAuth();
        authTokens.put(authToken, new AuthData(authToken, user.username()));
        return authToken;
    }
    //returns UserData
    public UserData getUser(String username)
    {
        return users.get(username);
    }
    //returns if username is in users
    public boolean userExists(String username)
    {
        return users.containsKey(username);
    }
    //returns true if username and password match
    public boolean checkPassword(String username, String password)
    {
        return users.get(username).password().equals(password);
    }

    //return new authToken
    public String createAuth()
    {
        return UUID.randomUUID().toString();
    }
    //return if authorization token exists
    public boolean getAuth(String authToken)
    {
        return authTokens.containsKey(authToken);
    }
    public void deleteAuth(String authToken)
    {
        authTokens.remove(authToken);
    }

    //returns gameID
    public int createGame(String gameName)
    {
        int gameID = ++gameCount;
        GameData gameData = new GameData(gameID,null,null,gameName, new ChessGame());
        games.put(gameID, gameData);
        return gameID;
    }
    //returns gameID
    public GameData getGame(int gameID)
    {
        return games.get(gameID);
    }
    //returns all gameData
    public ArrayList<GameData> listGames()
    {
        return new ArrayList<>(games.values());
    }

    //to join a game/make a move
    public void updateGame(GameData gameData) throws DataAccessException
    {
        if (!games.containsKey(gameData.gameID()))
            throw new DataAccessException("Game does not exist");
        games.put(gameData.gameID(), gameData);
    }

    //clears all data
    public void clear()
    {
        users.clear();
        games.clear();
        authTokens.clear();
        gameCount = 0;
    }
}