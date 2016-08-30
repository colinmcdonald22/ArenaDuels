package net.hcfactions.duels.listeners;

import net.hcfactions.duels.Duel;
import net.hcfactions.duels.DuelManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DuelQuitListener implements Listener {

    private DuelManager duelManager;

    public DuelQuitListener(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (duelManager.getDuel(event.getPlayer().getUniqueId()) != null) {
            Duel activeDuel = duelManager.getDuel(event.getPlayer().getUniqueId());
            // This line could probably be cleaned up a little bit, but I'd rather this than another endDuel method which would do this.
            duelManager.endDuel(activeDuel, Bukkit.getServer().getPlayer(activeDuel.getOtherPlayer(event.getPlayer().getUniqueId())));
        }
    }

}