package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.sql.SequelDataAccess;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.BadRequestException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SequelDataAccessTests
{
    private SequelDataAccess sequelDataAccess;

    @BeforeEach
    void setUp() throws Exception
    {
        sequelDataAccess = new SequelDataAccess();
        sequelDataAccess.clear();
    }

    @Test
    void newDaoIsEmpty()
    {
        assertTrue(sequelDataAccess.listGames().isEmpty());
    }

    //happy cases
    @Test
    void addAndGetUser()
    {
        try
        {
            sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        assertNotNull(sequelDataAccess);
        assertTrue(sequelDataAccess.userExists("Ben"));
        assertFalse(sequelDataAccess.userExists("Jack"));
        assertEquals("Ben", sequelDataAccess.getUserWithUsername("Ben").username());
        assertTrue(BCrypt.checkpw("password123", sequelDataAccess.getUserWithUsername("Ben").password()));
        assertEquals("ben2@gmail.com", sequelDataAccess.getUserWithUsername("Ben").email());
    }

    @Test
    void logInUser() throws DataAccessException {
        String authToken;
        try
        {
            authToken = sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
        UserData ben = new UserData("Ben", "password123", "ben2@gmail.com");
        assertNotEquals(authToken, sequelDataAccess.logInUser(ben));
    }

    @Test
    void userAlreadyExists()
    {
        try
        {
            sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        assertThrows(DataAccessException.class,() -> sequelDataAccess.createUser(createNewUser()));
    }

    @Test
    void userExists()
    {
        try
        {
            sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
        assertTrue(sequelDataAccess.userExists("Ben"));
        assertFalse(sequelDataAccess.userExists("Jack"));
    }

    @Test
    void getUserWithAuthToken()
    {
        String authToken;
        try
        {
            authToken = sequelDataAccess.createUser(createNewUser());
            assertNotNull(sequelDataAccess);
        assertEquals("Ben", sequelDataAccess.getUserWithUsername(sequelDataAccess.getUsernameWithAuthToken(authToken)).username());
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUserWithUsername()
    {
        var userData = createNewUser();
        try
        {
            sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
        var userData2 = sequelDataAccess.getUserWithUsername(userData.username());
        assertEquals(userData.username(), userData2.username());
        assertTrue(BCrypt.checkpw(userData.password(), userData2.password()));
        assertEquals(userData.email(), userData2.email());
    }

    @Test
    void clear() throws DataAccessException {
        try
        {
            sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        sequelDataAccess.clear();
        assertTrue(sequelDataAccess.listGames().isEmpty());
        assertFalse(sequelDataAccess.userExists("Ben"));
    }

    @Test
    void createAuthToken()
    {
        var token1 = sequelDataAccess.createAuth();
        var token2 = sequelDataAccess.createAuth();
        assertNotNull(token1);
        assertNotEquals(token1,token2);
    }

    @Test
    void getAuth()
    {
        String authToken;
        try
        {
            authToken = sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
        assertTrue(sequelDataAccess.getAuth(authToken));
        assertFalse(sequelDataAccess.getAuth("There is always another secret"));
    }

    @Test
    void deleteAuthToken()
    {
        var token = sequelDataAccess.createAuth();
        sequelDataAccess.deleteAuth(token);
        assertFalse(sequelDataAccess.getAuth(token));
    }

    @Test
    void checkPassword() throws DataAccessException
    {
        try
        {
            sequelDataAccess.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        assertTrue(sequelDataAccess.checkPassword("Ben", "password123"));
        assertFalse(sequelDataAccess.checkPassword("Ben", "123password"));
    }

    @Test
    void createAndUpdateGame() throws DataAccessException {
        var gameID = sequelDataAccess.createGame("testGame");
        assertEquals(new ChessGame(), sequelDataAccess.getGame(gameID).chessGame());
        try
        {
            ChessGame game2 = new ChessGame();
            game2.makeMove(new ChessMove(new ChessPosition(2,1),new ChessPosition(4,1)));
            assertNotEquals(game2, sequelDataAccess.getGame(gameID).chessGame());
        }
        catch (InvalidMoveException e)
        {
            System.out.println("Invalid Move");
        }
    }

    @Test
    void joinGame() throws DataAccessException {
        var gameID = sequelDataAccess.createGame("testGame");
        try
        {
            sequelDataAccess.createUser(createNewUser());
        } catch (DataAccessException e) {
            System.out.println("Username already taken");
        }
        sequelDataAccess.joinGame(gameID,"Ben", ChessGame.TeamColor.WHITE);
        assertTrue(sequelDataAccess.listGames().contains(sequelDataAccess.getGame(gameID)));
        assertEquals("Ben", sequelDataAccess.getGame(gameID).whiteUsername());
        sequelDataAccess.joinGame(gameID,"Jack", ChessGame.TeamColor.BLACK);
        assertEquals("Ben", sequelDataAccess.getGame(gameID).whiteUsername());
        assertEquals("Jack", sequelDataAccess.getGame(gameID).blackUsername());
    }

    @Test
    void listGames() throws DataAccessException {
        var gameID1 = sequelDataAccess.createGame("testGame1");
        var gameID2 = sequelDataAccess.createGame("testGame2");
        List<GameData> expected = new ArrayList<>(List.of(sequelDataAccess.getGame(gameID1), sequelDataAccess.getGame(gameID2)));
        var games = sequelDataAccess.listGames();
        assertEquals(expected, games);
        var gameID3 = sequelDataAccess.createGame("testGame3");
        List<GameData> notExpected = new ArrayList<>(List.of(sequelDataAccess.getGame(gameID3)));
        assertNotEquals(notExpected, games);
    }

    @Test
    void getGame() throws DataAccessException
    {
        var gameID = sequelDataAccess.createGame("testGame");
        GameData game1 = new GameData(1,null,null,"testGame",new ChessGame());
        GameData game2 = new GameData(1,"Ben","Jack","gameTest",new ChessGame());
        assertEquals(sequelDataAccess.getGame(gameID), game1);
        assertNotEquals(sequelDataAccess.getGame(gameID), game2);
    }

    @Test
    void gameExists() throws DataAccessException
    {
        var gameID = sequelDataAccess.createGame("testGame1");
        assertTrue(sequelDataAccess.gameExists(gameID));
        assertFalse(sequelDataAccess.gameExists(123));
    }

    public UserData createNewUser()
    {
        return new UserData("Ben", "password123", "ben2@gmail.com");
    }
}
//mysql -u root -p