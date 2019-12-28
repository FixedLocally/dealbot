package me.lkp111138.dealbot.game.exception;

public class ConcurrentGameException extends Exception {
    private final int gid;
    public ConcurrentGameException(int gid) {
        this.gid = gid;
    }

    public int getGid() {
        return gid;
    }
}
