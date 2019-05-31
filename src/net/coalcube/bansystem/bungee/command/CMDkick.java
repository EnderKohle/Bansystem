package net.coalcube.bansystem.bungee.command;

import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.bungee.util.TabCompleteUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CMDkick extends Command implements TabExecutor {

	public CMDkick(String name) {
		super(name);
	}

	@SuppressWarnings("deprecation")
	public static void noReasonKick(CommandSender p, ProxiedPlayer target) {
		target.disconnect(BanSystem.messages.getString("Kick.noreason.screen")
				.replaceAll("&", "§"));
		p.sendMessage(BanSystem.messages.getString("Kick.success")
				.replaceAll("%P%", BanSystem.PREFIX)
				.replaceAll("%player%", target.getDisplayName())
				.replaceAll("&", "§"));

		for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
			if (all.hasPermission("bansys.notify") && all != p) {
				for (String message : BanSystem.messages.getStringList("Kick.noreason.notify")) {
					all.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX)
							.replaceAll("%player%", target.getDisplayName())
							.replaceAll("%sender%", p.getName())
							.replaceAll("&", "§"));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void ReasonKick(CommandSender p, ProxiedPlayer target, String[] args) {
		p.sendMessage(BanSystem.messages.getString("Kick.success")
				.replaceAll("%P%", BanSystem.PREFIX)
				.replaceAll("%player%", target.getDisplayName())
				.replaceAll("&", "§"));
		String msg = "";
		for (int i = 1; i < args.length; i++) {
			msg = msg + args[i] + " ";
		}
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		target.disconnect(BanSystem.messages.getString("Kick.reason.screen").replaceAll("%P%", BanSystem.PREFIX)
				.replaceAll("%reason%", msg));
		for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
			if (all.hasPermission("bansys.notify") && all != p) {
				for (String message : BanSystem.messages.getStringList("Kick.reason.notify")) {
					all.sendMessage(
							message.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", target.getDisplayName())
									.replaceAll("%sender%", p.getName())
									.replaceAll("%reason%", msg)
									.replaceAll("&", "§"));
				}
			}
		}
	}

	public CMDkick(String name, BanSystem plugin) {
		super(name);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender p, String[] args) {
		if (p.hasPermission("bansys.kick")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
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
							p.sendMessage(BanSystem.messages.getString("Kick.cannotkickyouselfe").replaceAll("%P%",
									BanSystem.PREFIX).replaceAll("&", "§"));
						}
					} else {
						p.sendMessage(
								BanSystem.messages.getString("PlayerNotFound").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
					}
				} else if (args.length > 1) {
					ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
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
							p.sendMessage(BanSystem.messages.getString("Kick.cannotkickyouselfe").replaceAll("%P%",
									BanSystem.PREFIX).replaceAll("&", "§"));
						}
					} else {
						p.sendMessage(
								BanSystem.messages.getString("PlayerNotFound").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
					}
				} else {
					p.sendMessage(BanSystem.messages.getString("Kick.usage").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
				}
			} else {
				p.sendMessage(BanSystem.NODBCONNECTION);
			}
		} else {
			p.sendMessage(BanSystem.NOPERMISSION);
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return TabCompleteUtil.completePlayerNames(sender, args);
	}
}
