package net.hcfactions.duels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerWinDuelEvent extends PlayerEvent {

    private static HandlerList handlers = new HandlerList();

    public PlayerWinDuelEvent(Player player) {
        super(player);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}