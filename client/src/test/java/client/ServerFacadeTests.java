package client;

import dataaccess.DataAccess;
import dataaccess.sql.SequelDataAccess;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import server.Server;
import service.Service;s

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests
{
    private Service service;
    private static Server server;
    private static final ServerFacade facade = new ServerFacade("http://localhost:8080");

    @BeforeAll
    public static void init()
    {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    //happy cases
    @Test
    void register() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        assertFalse(dataAccess.userExists("Ben"));
        registerUser();
        assertTrue(dataAccess.userExists("Ben"));
    }

    @Test
    void login(){
        registerUser();
        loginUser();
    }

    @Test
    void logout(){
        registerUser();
        loginUser();
        facade.logout();
    }

    @Test
    void createGame() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        assertFalse(dataAccess.gameExists(1));
        registerUser();
        facade.create(new String[]{"game1"});
        assertTrue(dataAccess.gameExists(1));
    }

    @Test
    void listGames() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        assertEquals("SIGNEDIN", facade.list());
    }

    void registerUser() {
        facade.register(new String[]{"Ben", "password123", "email"});
    }
    void loginUser() {
        facade.login(new String[]{"Ben", "password123"});
    }
}