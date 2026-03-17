import chess.ChessGame;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyDatabaseTests {

    private static final TestUser TEST_USER = new TestUser("TestUser", "TestPassword", "test@mail.com");

    private static TestServerFacade serverFacade;

    private static Server server;

    private static Class<?> databaseManagerClass;


    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @BeforeEach
    public void setUp() {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("deleteAllUsers Test")
    @Order(1)
    public void clearUTest() {
        int initialRowCount = getDatabaseRows();

        TestAuthResult regResult = serverFacade.register(TEST_USER);
        String auth = regResult.getAuthToken();

        //create a game
        String gameName = "Test Game";
        TestResult createResult = serverFacade.createGame(new TestCreateRequest(gameName), auth);
        createResult = serverFacade.clear();
        Assertions.assertEquals(0, getDatabaseRows(), "Empty database");
    }
    @Test
    @DisplayName("deleteAllGames Test")
    @Order(2)
    public void clearGTest() {
        int initialRowCount = getDatabaseRows();

        TestAuthResult regResult = serverFacade.register(TEST_USER);
        String auth = regResult.getAuthToken();

        //create a game
        String gameName = "Test Game";
        TestResult createResult = serverFacade.createGame(new TestCreateRequest(gameName), auth);
        createResult = serverFacade.clear();
        Assertions.assertEquals(0, getDatabaseRows(), "Empty database");
    }
    @Test
    @DisplayName("deleteAllAuth Test")
    @Order(3)
    public void clearATest() {
        int initialRowCount = getDatabaseRows();

        TestAuthResult regResult = serverFacade.register(TEST_USER);
        String auth = regResult.getAuthToken();

        //create a game
        String gameName = "Test Game";
        TestResult createResult = serverFacade.createGame(new TestCreateRequest(gameName), auth);
        createResult = serverFacade.clear();
        Assertions.assertEquals(0, getDatabaseRows(), "Empty database");
    }
    @Test
    @DisplayName("deleteOneAuth Test")
    @Order(4)
    public void deleteOneAuthTest() {
        //Also the logout test negative
        int initialRowCount = getDatabaseRows();

        TestAuthResult regResult = serverFacade.register(TEST_USER);
        String auth = regResult.getAuthToken();
        serverFacade.logout(auth);
        //list games using the auth
        TestListResult listResult = serverFacade.listGames(auth);
        Assertions.assertEquals(401, serverFacade.getStatusCode(), "Didn't logout");
    }
    @Test
    @DisplayName("deleteOneAuth Test 2")
    @Order(5)
    public void deleteOneAuthTest2() {
        //Also the logout test positive
        int initialRowCount = getDatabaseRows();

        TestAuthResult regResult = serverFacade.register(TEST_USER);
        String auth = regResult.getAuthToken();
        //list games using the auth
        TestListResult listResult = serverFacade.listGames(auth);
        Assertions.assertEquals(200, serverFacade.getStatusCode(), "Logged out");
    }
    @Test
    @DisplayName("getAllGames positive")
    @Order(6)
    public void getAllGamesP() {
        //Also the logout test positive
        int initialRowCount = getDatabaseRows();

        TestAuthResult regResult = serverFacade.register(TEST_USER);
        String auth = regResult.getAuthToken();
        //create a game
        String gameName = "Test Game 1";
        TestCreateResult createResult = serverFacade.createGame(new TestCreateRequest(gameName), auth);
        String gameName2 = "Test Game 2";
        TestCreateResult createResult2 = serverFacade.createGame(new TestCreateRequest(gameName2), auth);

        //list games using the auth
        TestListResult listResult = serverFacade.listGames(auth);
        Assertions.assertEquals(200, serverFacade.getStatusCode(), "Server response code was not 200 OK");
        Assertions.assertEquals(2, listResult.getGames().length, "Missing game(s) in database after restart");
    }



    private int getDatabaseRows() {
        AtomicInteger rows = new AtomicInteger();
        executeForAllTables((tableName, connection) -> {
            try (var statement = connection.createStatement()) {
                var sql = "SELECT count(*) FROM " + tableName;
                try (var resultSet = statement.executeQuery(sql)) {
                    if (resultSet.next()) {
                        rows.addAndGet(resultSet.getInt(1));
                    }
                }
            }
        });

        return rows.get();
    }

    private void executeForAllTables(TableAction tableAction) {
        String sql = """
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_schema = DATABASE();
                """;

        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    tableAction.execute(resultSet.getString(1), conn);
                }
            }
        } catch (ReflectiveOperationException | SQLException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    private Connection getConnection() throws ReflectiveOperationException {
        Class<?> clazz = findDatabaseManager();
        Method getConnectionMethod = clazz.getDeclaredMethod("getConnection");
        getConnectionMethod.setAccessible(true);

        Object obj = clazz.getDeclaredConstructor().newInstance();
        return (Connection) getConnectionMethod.invoke(obj);
    }

    private Class<?> findDatabaseManager() throws ClassNotFoundException {
        if(databaseManagerClass != null) {
            return databaseManagerClass;
        }

        for (Package p : getClass().getClassLoader().getDefinedPackages()) {
            try {
                Class<?> clazz = Class.forName(p.getName() + ".DatabaseManager");
                clazz.getDeclaredMethod("getConnection");
                databaseManagerClass = clazz;
                return clazz;
            } catch (ReflectiveOperationException ignored) {}
        }
        throw new ClassNotFoundException("Unable to load database in order to verify persistence. " +
                "Are you using DatabaseManager to set your credentials? " +
                "Did you edit the signature of the getConnection method?");
    }

    @FunctionalInterface
    private interface TableAction {
        void execute(String tableName, Connection connection) throws SQLException;
    }

}
