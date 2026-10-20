package service.dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryDataAccessTests
{
    private MemoryDataAccess dataAccessObject;

    @BeforeEach
    void setUp()
    {
        dataAccessObject = new MemoryDataAccess();
        dataAccessObject.clear();
    }

    @Test
    void newDaoIsEmpty()
    {
        assertTrue(dataAccessObject.listGames().isEmpty());
    }

    @Test
    void addAndGetUser()
    {
        try
        {
            dataAccessObject.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        assertNotNull(dataAccessObject);
        assertTrue(dataAccessObject.userExists("Ben"));
        assertFalse(dataAccessObject.userExists("Jack"));
        assertEquals("Ben", dataAccessObject.getUserWithUsername("Ben").username());
        assertEquals("password123", dataAccessObject.getUserWithUsername("Ben").password());
        assertEquals("ben2@gmail.com", dataAccessObject.getUserWithUsername("Ben").email());
    }

    @Test
    void getUserWithAuthToken()
    {
        String authToken;
        try
        {
            authToken = dataAccessObject.createUser(createNewUser());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(dataAccessObject);
        assertEquals("Ben", dataAccessObject.getUserWithUsername(dataAccessObject.getUsernameWithAuthToken(authToken)).username());
    }

    @Test
    void clear()
    {
        try
        {
            dataAccessObject.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        dataAccessObject.clear();
        assertTrue(dataAccessObject.listGames().isEmpty());
        assertFalse(dataAccessObject.userExists("Ben"));
    }

    @Test
    void createAuthToken()
    {
        var token1 = dataAccessObject.createAuth();
        var token2 = dataAccessObject.createAuth();
        assertNotNull(token1);
        assertNotEquals(token1,token2);
    }


    @Test
    void createAndUpdateGame() throws InvalidMoveException
    {
        var gameID = dataAccessObject.createGame("testGame");
        assertEquals(new ChessGame(), dataAccessObject.getGame(gameID).chessGame());
        try
        {
            ChessGame game2 = new ChessGame();
            game2.makeMove(new ChessMove(new ChessPosition(2,1),new ChessPosition(4,1)));
            assertNotEquals(game2, dataAccessObject.getGame(gameID).chessGame());
        }
        catch (InvalidMoveException e)
        {
            System.out.println("Invalid Move");
        }
    }

    @Test
    void joinGame()
    {
        var gameID = dataAccessObject.createGame("testGame");
        try
        {
            dataAccessObject.createUser(createNewUser());
        } catch (DataAccessException e) {
            System.out.println("Username already taken");
        }
        dataAccessObject.joinGame(gameID,"Ben", ChessGame.TeamColor.WHITE);
        assertTrue(dataAccessObject.listGames().contains(dataAccessObject.getGame(gameID)));
        assertEquals("Ben", dataAccessObject.getGame(gameID).whiteUsername());
        dataAccessObject.joinGame(gameID,"Jack", ChessGame.TeamColor.BLACK);
        assertEquals("Ben", dataAccessObject.getGame(gameID).whiteUsername());
        assertEquals("Jack", dataAccessObject.getGame(gameID).blackUsername());
    }

    @Test
    void listGames()
    {
        var gameID1 = dataAccessObject.createGame("testGame1");
        var gameID2 = dataAccessObject.createGame("testGame2");
        List<GameData> expected = new ArrayList<GameData>(List.of(dataAccessObject.getGame(gameID1), dataAccessObject.getGame(gameID2)));
        var games = dataAccessObject.listGames();
        assertEquals(expected, games);
        var gameID3 = dataAccessObject.createGame("testGame3");
        List<GameData> notExpected = new ArrayList<GameData>(List.of(dataAccessObject.getGame(gameID3)));
        assertNotEquals(notExpected, games);
    }

    public UserData createNewUser()
    {
        return new UserData("Ben", "password123", "ben2@gmail.com");
    }
}
