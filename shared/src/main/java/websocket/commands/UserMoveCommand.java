package websocket.commands;

import chess.ChessMove;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserMoveCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private final ChessMove move;

    public UserMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove gameMove) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = gameMove;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public ChessMove getMove() {
        return move;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserMoveCommand that = (UserMoveCommand) o;
        return commandType == that.commandType && Objects.equals(authToken, that.authToken)
                && Objects.equals(gameID, that.gameID) && Objects.equals(move, that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandType, authToken, gameID, move);
    }
}
