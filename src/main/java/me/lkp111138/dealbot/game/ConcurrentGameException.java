package me.lkp111138.dealbot.game;

public class ConcurrentGameException extends Exception {
    private final Game game;

    ConcurrentGameException(Game g) {
        game = g;
    }

    public Game getGame() {
        return game;
    }
}
