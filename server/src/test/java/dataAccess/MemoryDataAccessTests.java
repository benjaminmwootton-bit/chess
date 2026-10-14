package dataAccess;

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
    private MemoryDataAccess DAO;

    @BeforeEach
    void setUp()
    {
        DAO = new MemoryDataAccess();
        DAO.clear();
    }

    @Test
    void newDaoIsEmpty()
    {
        assertTrue(DAO.listGames().isEmpty());
    }

    @Test
    void addAndGetUser()
    {
        try
        {
            DAO.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        assertNotNull(DAO);
        assertEquals("Ben",DAO.getUser("Ben").username());
        assertEquals("password123",DAO.getUser("Ben").password());
        assertEquals("benwootton2@gmail.com",DAO.getUser("Ben").email());
    }

    @Test
    void clear()
    {
        try
        {
            DAO.createUser(createNewUser());
        }
        catch (DataAccessException e)
        {
            System.out.println("Username already taken");
        }
        DAO.clear();
        assertTrue(DAO.listGames().isEmpty());
    }

    @Test
    void createAndUpdateGame() throws InvalidMoveException
    {
        var gameID = DAO.createGame("testGame");
        assertEquals(new ChessGame(), DAO.getGame(gameID).ChessGame());
        try
        {
            ChessGame game2 = new ChessGame();
            game2.makeMove(new ChessMove(new ChessPosition(2,1),new ChessPosition(4,1)));
            assertNotEquals(game2,DAO.getGame(gameID).ChessGame());
        }
        catch (InvalidMoveException e)
        {
            System.out.println("Invalid Move");
        }
    }

    @Test
    void listGames()
    {
        var gameID1 = DAO.createGame("testGame1");
        var gameID2 = DAO.createGame("testGame2");
        List<GameData> expected = new ArrayList<GameData>(List.of(DAO.getGame(gameID1),DAO.getGame(gameID2)));
        var games = DAO.listGames();
        assertEquals(expected, games);
        var gameID3 = DAO.createGame("testGame3");
        List<GameData> notExpected = new ArrayList<GameData>(List.of(DAO.getGame(gameID1),DAO.getGame(gameID2),DAO.getGame(gameID3)));
        assertNotEquals(notExpected, games);
    }

    @Test
    void createAuthToken()
    {
        var token1 = DAO.createAuth();
        var token2 = DAO.createAuth();
        assertNotNull(token1);
        assertNotEquals(token1,token2);
    }

    public UserData createNewUser()
    {
        return new UserData("Ben", "password123", "benwootton2@gmail.com");
    }
}
