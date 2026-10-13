package dataAccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
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
    void addUser()
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

    public UserData createNewUser()
    {
        return new UserData("Ben", "password123", "benwootton2@gmail.com");
    }
}
