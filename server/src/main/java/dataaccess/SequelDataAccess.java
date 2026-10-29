package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.json.JavalinGson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SequelDataAccess implements DataAccess
{
    public SequelDataAccess() throws Exception
    {
        configureDatabase();
    }

    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> authTokens = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameCount = 0;

    public String createUser(UserData user) throws DataAccessException
    {
        String authToken;
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement1 = "Select COUNT(*) FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement1))
            {
                preparedStatement.setString(1, user.username());
                try(var resultSet = preparedStatement.executeQuery())
                {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        throw new DataAccessException("User already exists");
                    }
                }
            }
            String statement2 = "INSERT INTO users (username, UserData) VALUES (?,?)";
            try (var preparedStatement = conn.prepareStatement(statement2))
            {
                String json = new Gson().toJson(user);
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, json);
                preparedStatement.executeUpdate();
            }
            String statement3 = "INSERT INTO authTokens (authToken, AuthData) VALUES (?,?)";
            authToken = createAuth();
            try (var preparedStatement = conn.prepareStatement(statement3))
            {
                String json = new Gson().toJson(new AuthData(authToken, user.username()));
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, json);
                preparedStatement.executeUpdate();
            }
        }
        catch (DataAccessException e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Database failure", e);
        }
        return authToken;
    }

    public String logInUser(UserData user) throws DataAccessException
    {
        String authToken = createAuth();
        authTokens.put(authToken, new AuthData(authToken, user.username()));
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "INSERT INTO authTokens (authToken, AuthData) VALUES (?,?)";
            authToken = createAuth();
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                String json = new Gson().toJson(new AuthData(authToken, user.username()));
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, json);
                preparedStatement.executeUpdate();
            }
        }
        catch (DataAccessException e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Database failure", e);
        }
        return authToken;
    }
    //returns UserData
    public UserData getUserWithUsername(String username)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "Select userdata FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery())
                {
                    if(resultSet.next())
                    {
                        var json = resultSet.getString("UserData");
                        return new Gson().fromJson(json, UserData.class);
                    }
                    return null;
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    //returns username
    public String getUsernameWithAuthToken(String authToken)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "Select AuthData FROM authTokens WHERE authToken = ?";
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery())
                {
                    if(resultSet.next())
                    {
                        var json = resultSet.getString("AuthData");
                        return new Gson().fromJson(json, AuthData.class).username();
                    }
                    return null;
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    //returns if username is in users
    public boolean userExists(String username)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "Select userdata FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery())
                {
                    return resultSet.next();
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    //returns true if username and password match
    public boolean checkPassword(String username, String password)
    {
        UserData user = getUserWithUsername(username);
        return user != null && user.password().equals(password);
    }

    //return new authToken
    public String createAuth()
    {
        return UUID.randomUUID().toString();
    }
    //return if authorization token exists
    public boolean getAuth(String authToken)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "Select authData FROM authTokens WHERE authToken = ?";
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery())
                {
                    return resultSet.next();
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    public void deleteAuth(String authToken)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "DELETE FROM authTokens WHERE authToken = ?";
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeQuery();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    //returns gameID
    public int createGame(String gameName) throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "INSERT INTO games (GameData) VALUES (?)";
            try (var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS))
            {
                preparedStatement.setString(1, gameName);
                preparedStatement.executeUpdate();
                try(var generateKeys = preparedStatement.getGeneratedKeys())
                {
                    if(generateKeys.next())
                    {
                        return generateKeys.getInt(1);
                    }
                    else
                    {
                        throw new SQLException("Game creation failed");
                    }
                }
            }
        }
        catch (DataAccessException e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Database failure", e);
        }
    }
    //returns gameID
    public GameData getGame(int gameID)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "Select gameData FROM games WHERE id = ?";
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                preparedStatement.setInt(1, gameID);
                try (var resultSet = preparedStatement.executeQuery())
                {
                    if(resultSet.next())
                    {
                        var json = resultSet.getString("GameData");
                        return new Gson().fromJson(json, GameData.class);
                    }
                    return null;
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    //returns all gameData
    public ArrayList<GameData> listGames()
    {
        ArrayList<GameData> games = new ArrayList<>();
        int i = 0;
        while(true)
        {
            var game = getGame(i);
            if(game != null){
                games.add(game);
                i++;
            }
            else{
                break;}
        }
        return games;
    }
    public boolean gameExists(int gameID) throws DataAccessException
    {
        int gameCount = 0;
        try (Connection conn = DatabaseManager.getConnection())
        {
            String statement = "SELECT MAX(id) FROM games";
            try (var preparedStatement = conn.prepareStatement(statement))
            {
                try (var resultSet = preparedStatement.executeQuery())
                {
                    gameCount = resultSet.getInt(1);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return gameID > 0 && gameID <= gameCount;
    }

    //to join a game/make a move
    public void updateGame(GameData gameData) throws DataAccessException
    {
        if (!games.containsKey(gameData.gameID())){
            throw new DataAccessException("Game does not exist");}
        games.put(gameData.gameID(), gameData);
    }
    public void joinGame(int gameID, String username, ChessGame.TeamColor color)
    {
        var game = getGame(gameID);
        GameData newGame;
        if(color == ChessGame.TeamColor.WHITE){
            newGame = new GameData(gameID,username,game.blackUsername(),game.gameName(),game.chessGame());}
        else{
            newGame = new GameData(gameID,game.whiteUsername(),username,game.gameName(),game.chessGame());}
        try
        {
            updateGame(newGame);
        }
        catch (Exception e)
        {
            System.out.println("GameID not found");
        }
    }
    //clears all data
    public void clear() throws DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            String[] statements = {"TRUNCATE users","TRUNCATE authTokens","TRUNCATE games"};
            for(String statement: statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void configureDatabase() throws Exception
    {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection())
        {
            for (String statement : createStatements)
            {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();}
            }
        }
        catch (SQLException ex)
        {
            throw new Exception(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private final String[] createStatements = {
            "CREATE TABLE IF NOT EXISTS users (username varchar(256) NOT NULL PRIMARY KEY, UserData TEXT DEFAULT NULL) DEFAULT CHARSET=utf8mb4",
            "CREATE TABLE IF NOT EXISTS authTokens (authToken varchar(256) NOT NULL PRIMARY KEY, AuthData TEXT DEFAULT NULL) DEFAULT CHARSET=utf8mb4",
            "CREATE TABLE IF NOT EXISTS games (id int NOT NULL AUTO_INCREMENT PRIMARY KEY, GameData TEXT DEFAULT NULL) DEFAULT CHARSET=utf8mb4"
    };
}