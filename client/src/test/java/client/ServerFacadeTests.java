package client;

import dataaccess.DataAccess;
import dataaccess.sql.SequelDataAccess;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import server.Server;
import service.Service;

import java.util.ArrayList;

import static client.State.SIGNEDIN;
import static client.State.SIGNEDOUT;
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
    }

    @BeforeEach
    public void setup() {facade.clear();}

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
        assertEquals(SIGNEDIN, facade.register(new String[]{"Ben", "password123", "email"}));
        assertTrue(dataAccess.userExists("Ben"));
    }

    @Test
    void login(){
        registerUser();
        assertEquals(SIGNEDIN, facade.login(new String[]{"Ben", "password123"}));
    }

    @Test
    void logout(){
        registerUser();
        loginUser();
        assertEquals(SIGNEDOUT, facade.logout());
    }

    @Test
    void createGame() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        assertFalse(dataAccess.gameExists(1));
        registerUser();
        assertEquals(SIGNEDIN, facade.create(new String[]{"game1"}));
        assertTrue(dataAccess.gameExists(1));
    }

    @Test
    void listGames(){
        registerUser();
        createNewGame();
        assertEquals(SIGNEDIN, facade.list());
    }

    @Test
    void joinGame() throws Exception{
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        createNewGame();
        assertEquals(SIGNEDIN, facade.join(new String[]{"white", "1"}));
        facade.register(new String[]{"Jack", "123password", "email"});
        assertEquals(SIGNEDIN, facade.join(new String[]{"black", "1"}));
        assertTrue(dataAccess.gameExists(1));
        assertEquals("Ben", dataAccess.getGame(1).whiteUsername());
        assertEquals("Jack", dataAccess.getGame(1).blackUsername());
    }

    @Test
    void clear() throws Exception{
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        createNewGame();
        facade.clear();
        assertEquals(new ArrayList<>(), dataAccess.listGames());
    }

    void registerUser() {
        facade.register(new String[]{"Ben", "password123", "email"});
    }
    void loginUser() {
        facade.login(new String[]{"Ben", "password123"});
    }
    void createNewGame() {
        facade.create(new String[]{"game1"});
    }
}