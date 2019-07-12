package net.coalcube.bansystem.bungee.command;

import java.util.UUID;

import net.coalcube.bansystem.bungee.util.TabCompleteUtil;
import net.coalcube.bansystem.bungee.util.Banmanager;
import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CMDunmute extends Command implements TabExecutor {

	public CMDunmute(String name) {
		super(name);
	}
	
	private static Banmanager bm = BanSystem.getBanmanager();
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("bansys.unmute")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(
								BanSystem.messages.getString("Playerdoesnotexist").replaceAll("%P%", BanSystem.PREFIX));
						return;
					}
					if (bm.isBannedChat(id)) {
						if(args.length > 1 && BanSystem.config.getBoolean("needReason.Unmute")) {
							
							String reason = "";
							for (int i = 1; i < args.length; i++) {
								reason = reason + args[i] + " ";
							}
							
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unmute.needreason.success")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%reason%", reason)));
							for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											BanSystem.messages.getString("Unmute.needreason.notify")
													.replaceAll("%P%", BanSystem.PREFIX)
													.replaceAll("%player%", UUIDFetcher.getName(id))
													.replaceAll("%sender%", sender.getName())
													.replaceAll("%reason%", reason)));
								}
							}
							ProxyServer.getInstance().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unmute.needreason.notify")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%sender%", sender.getName())
									.replaceAll("%reason%", reason)));
							
							if(sender instanceof ProxyServer) {
								ProxiedPlayer p = (ProxiedPlayer) sender;
								bm.unmute(id, bm.getID(id, Type.CHAT), p.getUniqueId() , reason);
							} else
								bm.unmute(id, bm.getID(id, Type.CHAT), sender.getName(), reason);
							
						} else {
							
							sender.sendMessage(BanSystem.messages.getString("Unmute.success")
									.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id)));
							for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											BanSystem.messages.getString("Unmute.notify")
													.replaceAll("%P%", BanSystem.PREFIX)
													.replaceAll("%player%", UUIDFetcher.getName(id))
													.replaceAll("%sender%", sender.getName())
													.replaceAll("&", "§")));
								}
							}
							ProxyServer.getInstance().getConsole().sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unmute.notify")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%sender%", sender.getName())));
							
							bm.unmute(id);
							
						}
					} else {
						sender.sendMessage(BanSystem.messages.getString("Unmute.notmuted")
								.replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("%player%", UUIDFetcher.getName(id))
								.replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(
							BanSystem.messages.getString("Unmute.usage")
								.replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("&", "§"));
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
