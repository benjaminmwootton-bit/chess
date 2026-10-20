package service.service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.AlreadyTakenException;
import server.BadRequestException;
import service.Service;
import service.requestsandresults.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests
{
    private Service service;

    @BeforeEach
    public void setUp() {
        service = new Service(new MemoryDataAccess());
    }

    //Happy cases
    @Test
    void delete() throws Exception{
        var result = service.delete();
        assertTrue(result.deleted());
    }

    @Test
    void register() throws Exception {
        var result = service.register(new RegisterRequest("Ben", "password123", "ben2@gmail.com"));
        assertNotNull(result);
        assertNotNull(result.authToken());
        assertEquals("Ben", result.username());
    }

    @Test
    void logIn() throws Exception {
        var testUser = registerTestUser();
        var result = service.logIn(new LogInRequest(testUser.username(), "password123"));
        assertNotNull(result);
        assertNotNull(result.authToken());
        assertEquals("Ben", result.username());
    }

    @Test
    void logOut() throws Exception {
        var testUser = registerTestUser();
        var result = service.logOut(new LogOutRequest(testUser.authToken()));
        assertNotNull(result);
        assertTrue(result.loggedOut());
    }

    @Test
    void createGame() throws Exception {
        var testUser = registerTestUser();
        var result = service.createGame(new CreateGameRequest(testUser.authToken(),"testGame"));
        assertNotNull(result);
    }

    @Test
    void listGames() throws Exception {
        var testUser = registerTestUser();
        var game1 = service.createGame(new CreateGameRequest(testUser.authToken(),"testGame1"));
        var data1 = new GameData(game1.gameID(),null,null, "testGame1", new ChessGame());
        var result = service.listGames(new ListGamesRequest(testUser.authToken()));
        assertNotNull(result);
        assertTrue(result.games().contains(data1));

        var game2 = service.createGame(new CreateGameRequest(testUser.authToken(),"testGame2"));
        var data2 = new GameData(game2.gameID(),null,null, "testGame2", new ChessGame());
        result = service.listGames(new ListGamesRequest(testUser.authToken()));
        assertTrue(result.games().contains(data1) && result.games().contains(data2));
    }

    @Test
    void joinGame() throws Exception {
        var testUser = registerTestUser();
        service.createGame(new CreateGameRequest(testUser.authToken(), "testGame"));
        var result = service.joinGame(new JoinGameRequest(testUser.authToken(), ChessGame.TeamColor.WHITE, 1));
        assertNotNull(result);
        assertTrue(result.joined());
    }

    @Test
    void registerMissingData() throws Exception {
        assertThrows(BadRequestException.class, () -> service.register(new RegisterRequest(null, "password123", "ben2@gmail.com")));
        assertThrows(BadRequestException.class, () -> service.register(new RegisterRequest("Ben", null, "ben2@gmail.com")));
        assertThrows(BadRequestException.class, () -> service.register(new RegisterRequest("Ben", "password123", null)));
    }

    @Test
    void userAlreadyExists() throws Exception{
        registerTestUser();
        assertThrows(AlreadyTakenException.class, this::registerTestUser);
    }


    private RegisterResult registerTestUser() throws Exception {
        return service.register(new RegisterRequest("Ben", "password123", "ben2@gmail.com"));
    }
}