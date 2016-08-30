package net.hcfactions.duels.commands;

import net.hcfactions.duels.DuelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {

    private DuelManager duelManager;

    public DuelCommand(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "The console cannot send duel requests!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.YELLOW + "/Duel ( " + ChatColor.BLUE + "Name" + ChatColor.YELLOW + " )");
            return true;
        }

        Player target = Bukkit.getServer().getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "No player by the name of " + args[0] + " found.");
            return true;
        }

        if (target == sender) {
            sender.sendMessage(ChatColor.RED + "You cannot duel yourself!");
            return true;
        }

        duelManager.sendDuelRequest((Player) sender, target);

        return true;
    }

}