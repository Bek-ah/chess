package dataaccess;

import com.google.gson.Gson;
import model.Auth;
import model.Game;
import model.User;
import passoff.exception.ResponseParseException;
import server.DataBase;

import java.sql.*;
import java.sql.SQLException;
import java.util.HashMap;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqulDataAccess implements DataAccess {
    public MySqulDataAccess() throws ResponseParseException {
        configureDatabase();
    };

    //CREATE
    public void createUser(User userData){
        var statement = "INSERT INTO userTable (username, user) VALUES (?, ?)";
        String json = new Gson().toJson(userData);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, json);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public Game createGame(String gn, int gID){
        return null;
    }
    public void createAuth(Auth newAuth){
    }
    //GET 1
    public User getUserbyUsername(String username){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, json FROM userTable WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseParseException("Unable to read data", e);
        }
        return null;
    }
    private User readUser(ResultSet rs) throws SQLException {
        var username = rs.getInt("username");
        var json = rs.getString("json");
        User user = new Gson().fromJson(json, User.class);
        return user;
    }
    public Game getGamebyGameID(int id){
        return null;

    }
    public Game getGamebyGameName(String gameName){
        return null;
    }
    public Auth getAuthbyToken(String token){
        return null;
    }
    //GET ALL
    public HashMap<Integer, Game> getAllGames(){
        return null;

    }
    //CLEAR 1
    public void deleteUser(String username) {
    }
    public void deleteAuth(String token){
    }
    public void deleteGame(int gameID) {
    }

    //CLEAR ALL
    public void deleteAllUsers() {
        var statement = "TRUNCATE userTable;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAllAuth(){
        var statement = "TRUNCATE authTable;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAllGames() {
        var statement = "TRUNCATE gameTable;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userTable (
              `username` VARCHAR(255) NOT NULL,
              `user` JSON,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  authTable (
              `username` VARCHAR(255) NOT NULL,
              `authToken` VARCHAR(255) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  gameTable (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(100),
              `blackUsername` VARCHAR(100),
              `gameName` VARCHAR(100) NOT NULL,
              `game` JSON,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """

    };
    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new NullPointerException("SQL Exception");
        }
    }

}
