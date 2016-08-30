package net.hcfactions.duels.events;

import net.hcfactions.duels.Duel;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class DuelEndEvent extends DuelEvent {

    private static HandlerList handlers = new HandlerList();

    public DuelEndEvent(Duel duel) {
        super(duel);
    }

    public UUID getWinner() {
        return getDuel().getWinner();
    }

    public UUID getLoser() {
        return getDuel().getLoser();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}