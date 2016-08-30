package net.hcfactions.duels;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Duel {

    private UUID player1;
    private UUID player2;

    private long started = -1;
    private long ended = -1;
    private boolean player1Won;

    public Duel(Player player1, Player player2) {
        this.player1 = player1.getUniqueId();
        this.player2 = player2.getUniqueId();
        this.started = System.currentTimeMillis();
    }

    public void end(UUID winner) {
        this.ended = System.currentTimeMillis();
        this.player1Won = player1.equals(winner);
    }

    public UUID getPlayer1() {
        return player1;
    }

    public UUID getPlayer2() {
        return player2;
    }

    public UUID getWinner() {
        if (ended == -1) {
            return null;
        }

        return player1Won ? player1 : player2;
    }

    public UUID getLoser() {
        if (ended == -1) {
            return null;
        }

        return player1Won ? player2 : player1;
    }

    public long getStartingTime() {
        return started;
    }

    public long getEndingTime() {
        return ended;
    }

    public long getDurationMillis() {
        return (ended == -1 ? -1 : ended - started);
    }

    // When given player1, it'll return player2. When given player2, it'll return player1.
    public UUID getOtherPlayer(UUID player) {
        return (player1 == player) ? player2 : player1;
    }

}