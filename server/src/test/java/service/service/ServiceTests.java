package service.service;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;
import service.requestsandresults.LogInRequest;
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
    }

    public RegisterResult registerTestUser() throws Exception {
        return service.register(new RegisterRequest("Ben", "password123", "ben2@gmail.com"));
    }
}