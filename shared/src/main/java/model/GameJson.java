package model;

import chess.ChessGame;import java.util.Objects;

public class GameJson {
    private Integer gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private transient ChessGame game;

    @Override
    public String toString() {
        return "GameJson{" +
                "gameID=" + gameID +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                ", game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameJson gameJson = (GameJson) o;
        return Objects.equals(gameID, gameJson.gameID) && Objects.equals(whiteUsername, gameJson.whiteUsername) && Objects.equals(blackUsername, gameJson.blackUsername) && Objects.equals(gameName, gameJson.gameName) && Objects.equals(game, gameJson.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getGameName() {
        return gameName;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public Integer getGameID() {
        return gameID;
    }

    public GameJson() {
    }
}
