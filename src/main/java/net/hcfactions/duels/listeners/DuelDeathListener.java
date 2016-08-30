package net.hcfactions.duels.listeners;

import net.hcfactions.duels.Duel;
import net.hcfactions.duels.DuelManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DuelDeathListener implements Listener {

    private DuelManager duelManager;

    public DuelDeathListener(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (duelManager.getDuel(event.getEntity().getUniqueId()) != null) {
            Duel activeDuel = duelManager.getDuel(event.getEntity().getUniqueId());
            // This line could probably be cleaned up a little bit, but I'd rather this than another endDuel method which would do this.
            duelManager.endDuel(activeDuel, Bukkit.getServer().getPlayer(activeDuel.getOtherPlayer(event.getEntity().getUniqueId())));
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

}