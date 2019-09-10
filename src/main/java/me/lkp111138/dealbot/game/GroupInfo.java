package me.lkp111138.dealbot.game;

public class GroupInfo {
    final int waitTime; // max wait time for each turn in secs
    final int payTime; // max wait time for paying
    final int sayNoTime; // max wait time for saying no

    public GroupInfo(int waitTime, int payTime, int sayNoTime) {
        this.waitTime = waitTime;
        this.payTime = payTime;
        this.sayNoTime = sayNoTime;
    }
}
