package net.hcfactions.duels.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class PlayerUtils {

    public static void resetInventory(Player player, GameMode gameMode) {
        player.setHealth(20);
        player.setFallDistance(0F);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0F);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFireTicks(0);
        player.setVelocity(new Vector());

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        if (gameMode != null && player.getGameMode() != gameMode) {
            player.setGameMode(gameMode);
        }
    }

}