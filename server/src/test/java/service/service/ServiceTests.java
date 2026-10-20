package service.service;

import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;
import service.requestsandresults.LogInRequest;
import service.requestsandresults.LogOutRequest;
import service.requestsandresults.RegisterRequest;
import service.requestsandresults.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests
{
    private Service service;

    @BeforeEach
    public void setUp() {
        service = new Service(new MemoryDataAccess());
    }

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

    }

    @Test
    void listGames() throws Exception {

    }

    @Test
    void joinGame() throws Exception {

    }

    public RegisterResult registerTestUser() throws Exception {
        return service.register(new RegisterRequest("Ben", "password123", "ben2@gmail.com"));
    }
}