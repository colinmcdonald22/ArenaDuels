package net.hcfactions.duels.events;

import net.hcfactions.duels.Duel;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DuelStartEvent extends DuelEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public DuelStartEvent(Duel duel) {
        super(duel);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}