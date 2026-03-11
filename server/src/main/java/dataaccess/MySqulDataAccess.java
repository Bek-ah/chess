package dataaccess;

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
    }
    public Game createGame(String gn, int gID){
        return null;
    }
    public void createAuth(Auth newAuth){
    }
    //GET 1
    public User getUserbyUsername(String username){
        return null;
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
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAllAuth(){
        var statement = "TRUNCATE authTable;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAllGames() {
        var statement = "TRUNCATE gameTable;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userTable (
              `username` VARCHAR(255),
              `password` VARCHAR(255) NOT NULL,
              `email` VARCHAR(255) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(email),
              UNIQUE(username),
              UNIQUE(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  authTable (
              `username` VARCHAR(255),
              `authToken` VARCHAR(255) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(authToken),
              UNIQUE(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  gameTable (
              `gameID` int NOT NULL,
              `whiteUsername` VARCHAR(100),
              `blackUsername` VARCHAR(100),
              `gameName` VARCHAR(100) NOT NULL,
              `game` JSON,
              PRIMARY KEY (`gameName`),
              INDEX(gameID),
              UNIQUE(`gameName`)
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
