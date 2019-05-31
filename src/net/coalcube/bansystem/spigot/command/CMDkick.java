package net.coalcube.bansystem.spigot.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.TabCompleteUtil;

public class CMDkick implements CommandExecutor, TabExecutor {

	@Override
	public boolean onCommand(CommandSender p, Command c, String lable, String[] args) {
		if (p.hasPermission("bansys.kick")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					Player target = Bukkit.getPlayer(args[0]);
					if (target != null) {
						if (target != p) {
							if (!target.hasPermission("bansys.kick")) {
								noReasonKick(p, target);

							} else {
								if (p.hasPermission("bansys.kick.admin")) {

									noReasonKick(p, target);

								} else {
									p.sendMessage(BanSystem.messages.getString("Kick.cannotkickteammembers")
											.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
								}
							}
						} else {
							p.sendMessage(BanSystem.messages.getString("Kick.cannotkickyouselfe")
									.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						}
					} else {
						p.sendMessage(BanSystem.messages.getString("PlayerNotFound").replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("&", "§"));
					}
				} else if (args.length > 1) {
					Player target = Bukkit.getPlayer(args[0]);
					if (target != null) {
						if (target != p) {
							if (!target.hasPermission("bansys.kick")) {
								ReasonKick(p, target, args);
							} else {
								if (p.hasPermission("bansys.kick.admin")) {

									ReasonKick(p, target, args);

								} else {
									p.sendMessage(BanSystem.messages.getString("Kick.cannotkickteammembers")
											.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
								}
							}
						} else {
							p.sendMessage(BanSystem.messages.getString("Kick.cannotkickyouselfe")
									.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						}
					} else {
						p.sendMessage(BanSystem.messages.getString("PlayerNotFound").replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("&", "§"));
					}
				} else {
					p.sendMessage(BanSystem.messages.getString("Kick.usage").replaceAll("%P%", BanSystem.PREFIX)
							.replaceAll("&", "§"));
				}
			} else {
				p.sendMessage(BanSystem.NODBCONNECTION);
			}
		} else {
			p.sendMessage(BanSystem.NOPERMISSION);
		}
		return false;
	}

	private void noReasonKick(CommandSender p, Player target) {
		target.kickPlayer(BanSystem.messages.getString("Kick.noreason.screen").replaceAll("&", "§"));
		p.sendMessage(BanSystem.messages.getString("Kick.success").replaceAll("%P%", BanSystem.PREFIX)
				.replaceAll("%player%", target.getDisplayName()).replaceAll("&", "§"));

		for (Player all : Bukkit.getOnlinePlayers()) {
			if (all.hasPermission("bansys.notify") && all != p) {
				for (String message : BanSystem.messages.getStringList("Kick.noreason.notify")) {
					all.sendMessage(
							message.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", target.getDisplayName())
									.replaceAll("%sender%", p.getName()).replaceAll("&", "§"));
				}
			}
		}
	}

	private void ReasonKick(CommandSender p, Player target, String[] args) {
		p.sendMessage(BanSystem.messages.getString("Kick.success").replaceAll("%P%", BanSystem.PREFIX)
				.replaceAll("%player%", target.getDisplayName()).replaceAll("&", "§"));
		String msg = "";
		for (int i = 1; i < args.length; i++) {
			msg = msg + args[i] + " ";
		}
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		target.kickPlayer(BanSystem.messages.getString("Kick.reason.screen").replaceAll("%P%", BanSystem.PREFIX)
				.replaceAll("%reason%", msg));
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (all.hasPermission("bansys.notify") && all != p) {
				for (String message : BanSystem.messages.getStringList("Kick.reason.notify")) {
					all.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX)
							.replaceAll("%player%", target.getDisplayName()).replaceAll("%sender%", p.getName())
							.replaceAll("%reason%", msg).replaceAll("&", "§"));
				}
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) {
		return TabCompleteUtil.completePlayerNames(sender, args);
	}
}
