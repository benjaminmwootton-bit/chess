package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

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
    void getUserWithAuthToken()
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
        assertNotNull(sequelDataAccess);
        assertEquals("Ben", sequelDataAccess.getUserWithUsername(sequelDataAccess.getUsernameWithAuthToken(authToken)).username());
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

    public UserData createNewUser()
    {
        return new UserData("Ben", "password123", "ben2@gmail.com");
    }
}
//mysql -u root -p