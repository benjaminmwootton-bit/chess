package client;

import dataaccess.DataAccess;
import dataaccess.sql.SequelDataAccess;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.ArrayList;

import static client.State.SIGNEDIN;
import static client.State.SIGNEDOUT;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests
{
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init()
    {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setup() {
        facade.clear();}

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
    void observe()
    {
        registerUser();
        createNewGame();
        assertEquals(SIGNEDIN, facade.observe(new String[]{"1"}));
    }

    @Test
    void clear() throws Exception{
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        createNewGame();
        facade.clear();
        assertEquals(new ArrayList<>(), dataAccess.listGames());
    }

    //sad cases
    @Test
    void registerWrongParameters() {
        assertEquals(SIGNEDOUT, facade.register(new String[]{"Ben"}));
        assertEquals(SIGNEDOUT, facade.register(new String[]{"Ben", "password123"}));
        assertEquals(SIGNEDOUT, facade.register(new String[]{"Ben", "password123", "email", "social security number"}));
    }

    @Test
    void registerUsernameTaken() {
        registerUser();
        assertEquals(SIGNEDOUT, facade.register(new String[]{"Ben", "password123", "email"}));
    }

    @Test
    void loginWrongParameters(){
        registerUser();
        assertEquals(SIGNEDOUT, facade.login(new String[]{"Ben"}));
        assertEquals(SIGNEDOUT, facade.login(new String[]{"Ben", "password123", "email"}));
    }

    @Test
    void loginBadRequest() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        assertEquals(SIGNEDOUT, facade.login(new String[]{"Ben", "123password"}));
        assertEquals(SIGNEDOUT, facade.login(new String[]{"Josh", "password123"}));
        assertEquals(new ArrayList<>(), dataAccess.listGames());
    }

    @Test
    void logoutNoUser(){
        assertEquals(SIGNEDOUT, facade.logout());
    }

    @Test
    void createGameWrongParameters() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        assertEquals(SIGNEDIN, facade.create(new String[]{}));
        assertEquals(SIGNEDIN, facade.create(new String[]{"game1", "Ben"}));
        assertEquals(new ArrayList<>(), dataAccess.listGames());
    }

    @Test
    void createGameNotAuthorized() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        assertEquals(SIGNEDIN, facade.create(new String[]{"game1"}));
        assertEquals(new ArrayList<>(), dataAccess.listGames());
    }

    @Test
    void listGamesNotAuthorized(){
        assertEquals(SIGNEDIN, facade.list());
    }

    @Test
    void joinGameBadRequest() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        createNewGame();
        assertEquals(SIGNEDIN, facade.join(new String[]{"1"}));
        assertEquals(SIGNEDIN, facade.join(new String[]{"1", "white"}));
        assertEquals(SIGNEDIN, facade.join(new String[]{"white", "1", "Ben"}));
        assertNotEquals("Ben", dataAccess.getGame(1).whiteUsername());
    }

    @Test
    void joinGameUnauthorized() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        createNewGame();
        facade.logout();
        assertEquals(SIGNEDIN, facade.join(new String[]{"white", "1"}));
        assertNull(dataAccess.getGame(1).whiteUsername());
    }

    @Test
    void joinGamePositionTaken() throws Exception {
        DataAccess dataAccess = new SequelDataAccess();
        registerUser();
        createNewGame();
        facade.join(new String[]{"white", "1"});
        facade.register(new String[]{"Jack", "123password", "email"});
        assertEquals(SIGNEDIN, facade.join(new String[]{"white", "1"}));
        assertEquals("Ben", dataAccess.getGame(1).whiteUsername());
        assertNotEquals("Jack", dataAccess.getGame(1).whiteUsername());
        assertNull(dataAccess.getGame(1).blackUsername());
    }

    @Test
    void observeWrongParameters()
    {
        registerUser();
        createNewGame();
        assertEquals(SIGNEDIN, facade.observe(new String[]{"1"}));
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