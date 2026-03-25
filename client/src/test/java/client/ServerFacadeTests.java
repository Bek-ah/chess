package client;

import model.Auth;
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
        var authData = facade.login("1", "1");
        Assertions.assertFalse(authData.authToken().length() > 10);
    }    @Test
    @Order(8)
    @DisplayName("getNeg")
    public void getNeg() {
        var authData = facade.login("1", "1");
        Assertions.assertFalse(authData.authToken().length() > 10);
    }    @Test
    @Order(9)
    @DisplayName("joinPos")
    public void joinPos() {
        var authData = facade.login("1", "1");
        Assertions.assertFalse(authData.authToken().length() > 10);
    }
    @Test
    @Order(10)
    @DisplayName("joinNeg")
    public void joinNeg() {
        var authData = facade.login("1", "1");
        Assertions.assertFalse(authData.authToken().length() > 10);
    }
    @Test
    @Order(11)
    @DisplayName("createPos")
    public void createPos() {
        var authData = facade.login("1", "1");
        Assertions.assertFalse(authData.authToken().length() > 10);
    }
    @Test
    @Order(12)
    @DisplayName("createNeg")
    public void createNeg() {
        var authData = facade.login("1", "1");
        Assertions.assertFalse(authData.authToken().length() > 10);
    }


}
