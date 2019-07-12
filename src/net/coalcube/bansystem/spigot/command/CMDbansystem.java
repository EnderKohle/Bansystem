package net.coalcube.bansystem.spigot.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.coalcube.bansystem.spigot.BanSystem;

public class CMDbansystem implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if(sender.hasPermission("bansys.bansys")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', BanSystem.messages.getString("bansystem.usage")).replaceFirst("%P%", BanSystem.PREFIX));
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("help")) {
					
					for(String s : BanSystem.messages.getStringList("banystem.help")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%P%", BanSystem.PREFIX));
					}
					
				} else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', BanSystem.messages.getString("bansystem.reload.process")).replaceAll("%P%", BanSystem.PREFIX));
					
					BanSystem.getInstance().onDisable();
					BanSystem.getInstance().onEnable();
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', BanSystem.messages.getString("bansystem.reload.finished")).replaceAll("%P%", BanSystem.PREFIX));
					
				} else if(args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', BanSystem.messages.getString("bansystem.version")).replaceAll("%P%", BanSystem.PREFIX).replaceAll("%ver%", BanSystem.getInstance().getDescription().getVersion()));
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', BanSystem.messages.getString("bansystem.usage")).replaceFirst("%P%", BanSystem.PREFIX));
			}
		} else {
			sender.sendMessage(BanSystem.PREFIX+"§7BanSystem by §eTobi§7.");
		}
		return false;
	}
}