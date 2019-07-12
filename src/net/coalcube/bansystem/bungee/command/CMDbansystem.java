package net.coalcube.bansystem.bungee.command;

import net.coalcube.bansystem.bungee.BanSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CMDbansystem extends Command {

	public CMDbansystem(String name) {
		super(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
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
	}

}
