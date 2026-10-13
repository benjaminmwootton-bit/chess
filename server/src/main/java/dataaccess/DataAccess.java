package dataaccess;

public interface DataAccess
{
    //returns authToken
    String createUser(String username, String password, String email);
    //returns UserData
    String getUser(String username);

    //return new authToken
    String createAuth();
    //return if authorization token exists
    boolean getAuth(String authToken);
    void deleteAuth(String authToken);

    //returns gameID
    String createGame();
    //returns gameID
    String getGame(String gameID);
    //returns all gameData
    String listGames();

    //to join a game/make a move
    void updateGame(String gameData);

    void clear();
}
