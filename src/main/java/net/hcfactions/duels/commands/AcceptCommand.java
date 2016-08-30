package net.hcfactions.duels.commands;

import net.hcfactions.duels.DuelInvite;
import net.hcfactions.duels.DuelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand implements CommandExecutor {

    private DuelManager duelManager;

    public AcceptCommand(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "The console cannot send duel requests!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.YELLOW + "/Accept ( " + ChatColor.BLUE + "Name" + ChatColor.YELLOW + " )");
            return true;
        }

        // It's better to do this anyway, as it doesn't require using exact names when accepting (like it did before)
        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "No player by the name of " + args[0] + " found.");
            return true;
        }

        if (target == sender) {
            sender.sendMessage(ChatColor.RED + "You cannot duel yourself!");
            return true;
        }

        DuelInvite invite = duelManager.getInvite(target.getUniqueId(), ((Player) sender).getUniqueId());

        if (invite == null) {
            sender.sendMessage(ChatColor.RED + "No matching duel invites found.");
            return true;
        }

        duelManager.acceptDuelRequest(invite);

        return true;
    }

}