package model;

import chess.ChessGame;import java.util.Objects;

public class Game {
    private Integer gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;

    public Game(Integer gID, String gn){
        this.gameID = gID;
        this.gameName = gn;
        this.game = new ChessGame();
    }
    public void setName(String newName){
        gameName = newName;
    }
@Override public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    Game game1 = (Game) o;
    boolean boo = Objects.equals(blackUsername, game1.blackUsername) && Objects.equals(gameName, game1.gameName) && Objects.equals(game, game1.game);
    return Objects.equals(gameID, game1.gameID) && Objects.equals(whiteUsername, game1.whiteUsername) && boo;
    }
    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }
    public boolean setWhitePlayer(String name){
        this.whiteUsername = name;
        return true;
    }
    public boolean setBlackPlayer(String name){
        this.blackUsername = name;
        return true;
    }
    public int getID(){
        return gameID;
    }
    public String getName() { return gameName; }
    public String getWhiteUsername(){ return whiteUsername; }
    public String getBlackUsername(){ return blackUsername; }
    public ChessGame getGame(){ return game; }

@Override public String toString() {
    return "Game{" +
            "gameID=" + gameID +
            ", whiteUsername='" + whiteUsername + '\'' +
            ", blackUsername='" + blackUsername + '\'' +
            ", gameName='" + gameName + '\'' +
            ", game=" + game +
            '}';
}}
