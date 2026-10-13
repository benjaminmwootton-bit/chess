package dataaccess;

public class MemoryDataAccess implements DataAccess
{
    //returns authToken
    public String createUser(String username, String password, String email)
    {
        return "not implemented";
    }
    //returns UserData
    public String getUser(String username)
    {
        return "not implemented";
    }

    //return new authToken
    public String createAuth()
    {
        return "not implemented";
    }
    //return if authorization token exists
    public boolean getAuth(String authToken)
    {
        return false;
    }
    public void deleteAuth(String authToken)
    {

    }

    //returns gameID
    public String createGame()
    {
        return "not implemented";
    }
    //returns gameID
    public String getGame(String gameID)
    {
        return "not implemented";
    }
    //returns all gameData
    public String listGames()
    {
        return "not implemented";
    }

    //to join a game/make a move
    public void updateGame(String gameData)
    {

    }

    public void clear()
    {

    }
}