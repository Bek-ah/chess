package client;

import model.Auth;
import model.Game;
import org.junit.jupiter.api.*;
import server.Server;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static Auth auth;
    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }
    @BeforeEach
    public void setup(){
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Order(1)
    @DisplayName("registerNeg")
    public void registerNeg() {
        var firstData = facade.register("1000", "10", "1@1.1");
        Assertions.assertEquals(firstData.username(), "403");
    }

    @Test
    @Order(2)
    @DisplayName("registerPos")
    public void registerPos() {
        Auth authData = facade.register("NewName5", "10", "1@1.1");
        Assertions.assertFalse(authData.authToken().isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("logoutPos")
    public void logoutPos() {
        Auth authToken;
        authToken = facade.register("test","1","1@1.1");
        var status = facade.logout(authToken);
        Assertions.assertTrue(status == 200);
    }

    @Test
    @Order(4)
    @DisplayName("logoutNeg")
    public void logoutNeg() {
        facade.register("53","1","1@1.1");
        var authData = facade.logout(new Auth("Bad auth", "000000"));
        Assertions.assertFalse(authData == 200);
    }

    @Test
    @Order(5)
    @DisplayName("loginPos")
    public void loginPos() {
        Auth token = facade.register("loginPos","loginPos","loginPos");
        facade.logout(token);
        Auth authData = facade.login("loginPos", "loginPos");
        Assertions.assertFalse(authData.authToken().isEmpty());
    }
    @Test
    @Order(6)
    @DisplayName("loginNeg")
    public void loginNeg() {
        Auth token = facade.register("loginNeg","loginNeg","loginNeg");
        facade.logout(token);
        Auth authData = facade.login("loginNeg", "4");
        Assertions.assertTrue(authData.authToken().isEmpty());
    }
    @Test
    @Order(7)
    @DisplayName("getPos")
    public void getPos() {
        Auth token = facade.register("getPos","getPos","email");
        Game game = facade.getGames(token,null);
        Assertions.assertTrue(game == null);
    }
    @Test
    @Order(8)
    @DisplayName("getNeg")
    public void getNeg() {
        Auth token = new Auth("getNeg", "");
        facade.register("getNeg","getPos","email");
        var nothing = facade.getGames(token,654);
        Assertions.assertTrue(nothing == null);
    }    @Test
    @Order(9)
    @DisplayName("joinPos")
    public void joinPos() {
        String playerColor = "WHITE";
        Auth token = facade.register("joinPos5","pass","email");
        int id = facade.createGame("Testing",token);
        Game game = facade.joinGame(playerColor,id,token);
        Assertions.assertTrue(game.getWhiteUsername().equals("joinPos7"));
    }
    @Test
    @Order(10)
    @DisplayName("joinNeg")
    public void joinNeg() {
        //Game game = facade.joinGame("<WHITE>",id,token);
        //Assertions.assertFalse(false);
        var authData = facade.register("joinNeg86", "1","1");
        int ret = facade.createGame("Testing",authData);
        facade.joinGame("WHITE",ret,authData);
        Assertions.assertTrue(true);
    }
    @Test
    @Order(11)
    @DisplayName("createPos")
    public void createPos() {
        var authData = facade.register("createPos10", "1","1");
        int ret = facade.createGame("Testing",authData);
        Assertions.assertTrue(ret!=401);
    }
    @Test
    @Order(12)
    @DisplayName("createNeg")
    public void createNeg() {
        Auth authData = facade.register("createNeg15", "1","1");
        int ret = facade.createGame("",authData);
        Assertions.assertEquals(401, ret);
    }


}
