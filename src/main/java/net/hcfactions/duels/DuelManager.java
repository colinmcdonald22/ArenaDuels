package net.hcfactions.duels;

import net.hcfactions.duels.commands.AcceptCommand;
import net.hcfactions.duels.commands.DuelCommand;
import net.hcfactions.duels.events.DuelEndEvent;
import net.hcfactions.duels.events.DuelStartEvent;
import net.hcfactions.duels.events.PlayerLoseDuelEvent;
import net.hcfactions.duels.events.PlayerWinDuelEvent;
import net.hcfactions.duels.listeners.DuelDeathListener;
import net.hcfactions.duels.listeners.DuelQuitListener;
import net.hcfactions.duels.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.kitteh.vanish.VanishPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DuelManager {

    private DuelsPlugin mainInstance;
    private Set<Duel> activeDuels = new HashSet<Duel>();
    private Set<DuelInvite> activeInvites = new HashSet<DuelInvite>();
    private VanishPlugin vanishPlugin;

    public DuelManager(DuelsPlugin mainInstance) {
        this.mainInstance = mainInstance;
        mainInstance.getServer().getPluginManager().registerEvents(new DuelDeathListener(this), mainInstance);
        mainInstance.getServer().getPluginManager().registerEvents(new DuelQuitListener(this), mainInstance);
        mainInstance.getCommand("Duel").setExecutor(new DuelCommand(this));
        mainInstance.getCommand("Accept").setExecutor(new AcceptCommand(this));

        new BukkitRunnable() {

            public void run() {
                HashSet<DuelInvite> remove = new HashSet<DuelInvite>();

                for (DuelInvite invite : activeInvites) {
                    if (invite.isExpired()) {
                        remove.add(invite); // We're not directly removing due to CMEs. We could use a ListIterator here, but I prefer this method.

                        Player inviter = Bukkit.getServer().getPlayer(invite.getInviter()); // We only want to inform the person who sent the invite.

                        if (inviter != null) {
                            inviter.sendMessage(ChatColor.RED + "Your duel invite with " + invite.getInvited() + " has expired.");
                        }
                    }
                }

                for (DuelInvite invite : remove) {
                    activeInvites.remove(invite);
                }
            }

        }.runTaskTimer(mainInstance, 20L, 20L);
    }

    // Invitations

    public void sendDuelRequest(Player inviter, Player invited) {
        if (getDuel(inviter.getUniqueId()) != null) {
            inviter.sendMessage(ChatColor.RED + "You are already in a duel.");
            return;
        }

        if (getDuel(invited.getUniqueId()) != null) {
            inviter.sendMessage(ChatColor.RED + invited.getName() + " is already in a duel.");
            return;
        }

        if (getInvite(inviter.getUniqueId(), invited.getUniqueId()) != null) {
            inviter.sendMessage(ChatColor.RED + "You've already sent " + invited.getName() + " a duel invite in the past " + DuelsPlugin.duelInviteTimeout + " seconds.");
            return;
        }

        activeInvites.add(new DuelInvite(inviter, invited));

        inviter.sendMessage(ChatColor.GREEN + "You have sent " + invited.getName() + " a duel invite. They have " + DuelsPlugin.duelInviteTimeout + " seconds to accept.");
        invited.sendMessage(ChatColor.GREEN +  inviter.getName() + " has sent you a duel invite. Type '/accept " + inviter.getName() + "' within " + DuelsPlugin.duelInviteTimeout + " seconds to accept.");
    }

    public void acceptDuelRequest(DuelInvite invite) {
        Player inviter = Bukkit.getServer().getPlayer(invite.getInviter());
        Player invited = Bukkit.getServer().getPlayer(invite.getInvited());

        activeInvites.remove(invite);

        if (inviter == null) {
            // Potentially blocking.
            invited.sendMessage(ChatColor.RED + Bukkit.getServer().getOfflinePlayer(invite.getInviter()).getName() + " has disconnected since they sent you a duel invite.");
            return;
        }

        if (getDuel(inviter.getUniqueId()) != null) {
            invited.sendMessage(ChatColor.RED + inviter.getName() + " is already in a duel.");
            return;
        }

        if (getDuel(invited.getUniqueId()) != null) {
            invited.sendMessage(ChatColor.RED + "You cannot accept a duel request while in a duel!");
            return;
        }

        startDuel(inviter, invited);
    }

    // Starting / stopping

    public void startDuel(Player player1, Player player2) {
        if (getDuel(player1.getUniqueId()) != null || getDuel(player2.getUniqueId()) != null) {
            mainInstance.getLogger().warning("startDuel was called on a player who already is in a duel. Aborting...");
            return;
        }

        Duel duel = new Duel(player1, player2);

        DuelStartEvent event = new DuelStartEvent(duel);
        mainInstance.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            mainInstance.getLogger().warning("A duel between " + player1.getName() + " and " + player2.getName() + " was cancelled by another plugin. Aborting...");
            return;
        }

        activeDuels.add(duel);
        mainInstance.getLogger().info("Starting duel between " + duel.getPlayer1() + " and " + duel.getPlayer2() + "...");

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (onlinePlayer != player1 && onlinePlayer != player2) {
                player1.hidePlayer(onlinePlayer);
                player2.hidePlayer(onlinePlayer);
            }
        }

        // Teleport the players (player1 and player2) to where they should be in the map. Make sure to delete the lines below.
        player1.sendMessage(ChatColor.GREEN + "You would've been teleported to the map!");
        player2.sendMessage(ChatColor.GREEN + "You would've been teleported to the map!");

        PlayerUtils.resetInventory(player1, GameMode.SURVIVAL);
        PlayerUtils.resetInventory(player2, GameMode.SURVIVAL);

        // Give the players (player1 and player2) kits. Make sure to delete the lines below.
        player1.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        player2.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        player1.sendMessage(ChatColor.GREEN + "You would've been given a kit!");
        player2.sendMessage(ChatColor.GREEN + "You would've been given a kit!");
    }

    public void endDuel(Duel duel, Player winner) {
        duel.end(winner.getUniqueId());

        mainInstance.getServer().getPluginManager().callEvent(new DuelEndEvent(duel));
        mainInstance.getServer().getPluginManager().callEvent(new PlayerWinDuelEvent(Bukkit.getServer().getPlayer(duel.getWinner())));
        mainInstance.getServer().getPluginManager().callEvent(new PlayerLoseDuelEvent(Bukkit.getServer().getPlayer(duel.getLoser())));

        activeDuels.remove(duel);
        mainInstance.getLogger().info("Ending duel between " + duel.getPlayer1() + " and " + duel.getPlayer2() + "... " + duel.getWinner() + " won.");

        Player loser = Bukkit.getServer().getPlayer(duel.getLoser());

        int winnerPotions = countPotions(winner.getInventory());
        int loserPotions = countPotions(loser.getInventory());

        winner.sendMessage(ChatColor.GREEN + "You defeated " + loser.getName() + " with " + (int) winner.getHealth() + "/20 health and " + winnerPotions + " potion" + (winnerPotions == 1 ? "" : "s") + ".");
        winner.sendMessage(ChatColor.GREEN + loser.getName() + " had " + loserPotions + " potion" + (loserPotions == 1 ? "" : "s") + ".");

        loser.sendMessage(ChatColor.GREEN + "You died to " + winner.getName() + ". " + winner.getName() + " had " + (int) winner.getHealth() + "/20 health and " + winnerPotions + " potion" + (winnerPotions == 1 ? "" : "s") + ".");
        loser.sendMessage(ChatColor.GREEN + "You had " + loserPotions + " potion" + (loserPotions == 1 ? "" : "s") + ".");

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            // Currently isVanished always returns false. It'll need to be updated to use VNP's API.
            if (isVanished(onlinePlayer)) {
                continue;
            }

            winner.showPlayer(onlinePlayer);
            loser.showPlayer(onlinePlayer);
        }

        winner.setHealth(winner.getMaxHealth());
        // Teleport the winner to spawn here. Make sure to delete the line below.
        winner.sendMessage(ChatColor.GREEN + "You would've been teleported to the spawn!");

        // Any database queries should also be executed at this time.
        new BukkitRunnable() {

            public void run() {

            }

        }.runTaskAsynchronously(mainInstance);
    }

    public Duel getDuel(UUID player) {
        for (Duel duel : activeDuels) {
            if (duel.getPlayer1() == player || duel.getPlayer2() == player) {
                return duel;
            }
        }

        return null;
    }

    public DuelInvite getInvite(UUID inviter, UUID invited) {
        for (DuelInvite duelInvite : activeInvites) {
            if (duelInvite.getInviter() == inviter && duelInvite.getInvited() == invited) {
                return duelInvite;
            }
        }

        return null;
    }

    public boolean isVanished(Player player) {
        if (vanishPlugin == null) {
            vanishPlugin = (VanishPlugin) Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket");

            if (vanishPlugin == null) {
                return false; // VanishNoPacket isn't installed.
            }
        }

        return vanishPlugin.getManager().isVanished(player);
    }

    public int countPotions(Inventory inventory) {
        ItemStack[] items = inventory.getContents();
        int potions = 0;

        for (int i = 0; i < items.length; i++) {
           if (items[i] != null && items[i].getType() == Material.POTION && items[i].getDurability()  == 16421) {
               potions++;
           }
        }

        return potions;
    }

}