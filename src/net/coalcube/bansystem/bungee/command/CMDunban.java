package net.coalcube.bansystem.bungee.command;

import java.util.UUID;

import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.bungee.util.TabCompleteUtil;
import net.coalcube.bansystem.bungee.util.Banmanager;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CMDunban extends Command implements TabExecutor {

	public CMDunban(String name) {
		super(name);
	}

	private static Banmanager bm = BanSystem.getBanmanager();

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("bansys.unban")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(
								BanSystem.messages.getString("Playerdoesnotexist")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("&", "§"));
						return;
					}
					if (bm.isBannedNetwork(id)) {
						if(args.length > 1 && BanSystem.config.getBoolean("needReason.Unban")) {
							
							String reason = "";
							for (int i = 1; i < args.length; i++) {
								reason = reason + args[i] + " ";
							}
							
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unban.needreason.success")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%reason%", reason)));
							for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											BanSystem.messages.getString("Unban.needreason.notify")
													.replaceAll("%P%", BanSystem.PREFIX)
													.replaceAll("%player%", UUIDFetcher.getName(id))
													.replaceAll("%sender%", sender.getName())
													.replaceAll("%reason%", reason)));
								}
							}
							ProxyServer.getInstance().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unban.needreason.notify")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%sender%", sender.getName())
									.replaceAll("%reason%", reason)));
							
							if(sender instanceof ProxiedPlayer) {
								ProxiedPlayer p = (ProxiedPlayer) sender;
								bm.unban(id, bm.getID(id, Type.NETWORK), p.getUniqueId() , reason);
							} else
								bm.unban(id, bm.getID(id, Type.NETWORK), sender.getName(), reason);
							
						} else {
							
							sender.sendMessage(BanSystem.messages.getString("Unban.success")
									.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id)));
							for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											BanSystem.messages.getString("Unban.notify")
													.replaceAll("%P%", BanSystem.PREFIX)
													.replaceAll("%player%", UUIDFetcher.getName(id))
													.replaceAll("%sender%", sender.getName())
													.replaceAll("&", "§")));
								}
							}
							ProxyServer.getInstance().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unban.notify")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%sender%", sender.getName())));
							
							bm.unban(id);
							
						}
					} else {
						sender.sendMessage(BanSystem.messages.getString("Unban.notbanned").replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(BanSystem.messages.getString("Unban.usage").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
				}
			} else {
				sender.sendMessage(BanSystem.NODBCONNECTION);
			}
		} else {
			sender.sendMessage(BanSystem.NOPERMISSION);
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return TabCompleteUtil.completePlayerNames(sender, args);
	}
}
