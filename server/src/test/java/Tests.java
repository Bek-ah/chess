import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Tests {

    private static TestUser existingUser;
    private static TestUser newUser;
    private static TestCreateRequest createRequest;
    private static TestServerFacade serverFacade;
    private static Server server;
    private String existingAuth;

    // ### TESTING SETUP/CLEANUP ###

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
        existingUser = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new TestUser("NewUser", "newUserPassword", "nu@mail.com");
        createRequest = new TestCreateRequest("testGame");
    }

    @BeforeEach
    public void setup() {
        serverFacade.clear();

        //one user already logged in
        TestAuthResult regResult = serverFacade.register(existingUser);
        existingAuth = regResult.getAuthToken();
    }

    @DisplayName("LoginEasy")
    public void loginSuccess() {
        TestAuthResult loginResult = serverFacade.login(existingUser);

        Assertions.assertEquals(existingUser.getUsername(), loginResult.getUsername(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(loginResult.getAuthToken(), "Response did not return authentication String");
    }

    @DisplayName("ClearTest")
    public void clearData() {
        //create filler games
        serverFacade.createGame(new TestCreateRequest(""), existingAuth);
        serverFacade.createGame(new TestCreateRequest("Not an empty gameName"), existingAuth);

        TestResult clearResult = serverFacade.clear();

    }
}
