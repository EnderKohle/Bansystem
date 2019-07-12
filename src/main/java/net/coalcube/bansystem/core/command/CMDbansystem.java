package net.coalcube.bansystem.core.command;

import net.coalcube.bansystem.core.BanSystem;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.User;
import net.md_5.bungee.api.ChatColor;

public class CMDbansystem implements Command {

	private Config messages;

	public CMDbansystem(Config messages) {
		this.messages = messages;
	}

	@Override
	public void execute(User sender, String[] args) {
		if (sender.hasPermission("bansys.bansys")) {
			if (args.length == 0) {
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', messages.getString("bansystem.usage"))
								.replaceFirst("%P%", messages.getString("Prefix")));
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {

					for (String s : messages.getStringList("banystem.help")) {
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', s).replaceAll("%P%", messages.getString("Prefix")));
					}

				} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					sender.sendMessage(ChatColor
							.translateAlternateColorCodes('&', messages.getString("bansystem.reload.process"))
							.replaceAll("%P%", messages.getString("Prefix")));

					BanSystem.getInstance().onDisable();
					BanSystem.getInstance().onEnable();

					sender.sendMessage(ChatColor
							.translateAlternateColorCodes('&',
									messages.getString("bansystem.reload.finished"))
							.replaceAll("%P%", messages.getString("Prefix")));

				} else if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
					sender.sendMessage(ChatColor
							.translateAlternateColorCodes('&', messages.getString("bansystem.version"))
							.replaceAll("%P%", messages.getString("Prefix"))
							.replaceAll("%ver%", BanSystem.getInstance().getVersion()));
				}
			} else {
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', messages.getString("bansystem.usage"))
								.replaceFirst("%P%", messages.getString("Prefix")));
			}
		} else {
			sender.sendMessage(messages.getString("Prefix") + "§7BanSystem by §eTobi§7.");
		}
	}

}
