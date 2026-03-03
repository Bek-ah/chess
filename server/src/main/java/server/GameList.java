package server;

import model.Game;

import java.util.Collection;

public record GameList (Collection<Game> Games) {}
