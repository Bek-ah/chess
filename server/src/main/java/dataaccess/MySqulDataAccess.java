package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.Auth;
import model.Game;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import passoff.exception.ResponseParseException;

import java.sql.*;
import java.sql.SQLException;
import java.util.HashMap;


public class MySqulDataAccess implements DataAccess {
    public MySqulDataAccess() throws ResponseParseException {
        configureDatabase();
    };
    public void updatePlayers(String whiteUsername, String blackUsername, Integer gameID){
        var statement = "UPDATE gameTable SET `whiteUsername` = ?, `blackUsername` = ? WHERE `gameID` = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, whiteUsername);
                preparedStatement.setString(2, blackUsername);
                preparedStatement.setInt(3, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //CREATE
    public void createUser(User userData){
        var statement = "INSERT INTO userTable (`username`, `user`) VALUES (?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        User secureUser = new User(userData.username(),hashedPassword, userData.email());
        String json = new Gson().toJson(secureUser);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, secureUser.username());
                preparedStatement.setString(2, json);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public Game createGame(String gn, int gID){
        gn = new Gson().fromJson(gn, Game.class).getName();
        var statement = "INSERT INTO gameTable (`gameName`, `gameID`, `game`) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                Game newGame = new Game(gID, gn);
                String json = new Gson().toJson(newGame);
                preparedStatement.setString(1, gn);
                preparedStatement.setInt(2, gID);
                preparedStatement.setString(3, json);
                preparedStatement.executeUpdate();
                return new Game(gID, gn);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void createAuth(Auth newAuth){
        var statement = "INSERT INTO authTable (`username`, `authToken`) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, newAuth.username());
                preparedStatement.setString(2, newAuth.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    //GET 1
    public User getUserbyUsername(String myUsername){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT `username`, `user` FROM userTable WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, myUsername);
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
        var json = rs.getString("user");
        User user = new Gson().fromJson(json, User.class);
        return user;
    }
    private Game readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String gameName = rs.getString("gameName");
        Game newGame = new Game(gameID, gameName);
        newGame.setWhitePlayer(rs.getString("whiteUsername"));
        newGame.setBlackPlayer(rs.getString("blackUsername"));
        String tempGame = rs.getString("game");
        ChessGame game = new Gson().fromJson(tempGame, ChessGame.class);
        return newGame;
    }
    private Auth readAuth(ResultSet rs) throws SQLException {
        var token = rs.getString("authToken");
        var userName = rs.getString("username");
        return new Auth(userName, token);
    }
    public Game getGamebyGameID(int id){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gameTable WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseParseException("Unable to read data", e);
        }
        return null;
    }
    public Game getGamebyGameName(String gameName){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT `gameName`, `game` FROM gameTable WHERE gameName=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseParseException("Unable to read data", e);
        }
        return null;
    }
    public Auth getAuthbyToken(String token){
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT `username`, `authToken` FROM authTable WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseParseException("Unable to read data", e);
        }
        return null;
    }
    //GET ALL
    public HashMap<Integer, Game> getAllGames(){
        var result = new HashMap<Integer, Game>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gameTable";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Game game = readGame(rs);
                        result.put(game.getID(),game);
                    }
                }
            }
        } catch (Exception e) {
            throw new NullPointerException("Data Access exception getAllGames");
        }
        return result;
    }
    //CLEAR 1
    public void deleteUser(String username) {
        var statement = "DELETE FROM userTable WHERE username=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteAuth(String token){
        var statement = "DELETE FROM authTable WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, token);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteGame(int gameID) {
        var statement = "DELETE FROM gameTable WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
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
              PRIMARY KEY (`authToken`),
              INDEX(username)
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
