package net.hcfactions.duels;

import net.hcfactions.duels.DuelsPlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelInvite {

    private UUID inviter; // This probably the wrong word to use for 'one who invited', however I can't think of a much better word.
    private UUID invited;

    private long sent = -1;
    private long expires = -1;

    public DuelInvite(Player inviter, Player invited) {
        this.inviter = inviter.getUniqueId();
        this.invited = invited.getUniqueId();

        this.sent = System.currentTimeMillis();
        this.expires = sent + (DuelsPlugin.duelInviteTimeout * 1000);
    }

    public UUID getInviter() {
        return inviter;
    }

    public UUID getInvited() {
        return invited;
    }

    public long getSentTime() {
        return sent;
    }

    public long getExpirationTime() {
        return expires;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expires;
    }

}