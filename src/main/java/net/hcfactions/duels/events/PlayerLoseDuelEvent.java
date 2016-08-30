package net.hcfactions.duels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerLoseDuelEvent extends PlayerEvent {

    private static HandlerList handlers = new HandlerList();

    public PlayerLoseDuelEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}