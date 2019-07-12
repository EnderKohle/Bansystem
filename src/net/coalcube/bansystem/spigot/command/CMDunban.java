package net.coalcube.bansystem.spigot.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.Banmanager;
import net.coalcube.bansystem.spigot.util.TabCompleteUtil;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;

public class CMDunban implements CommandExecutor, TabExecutor {

	private static Banmanager bm = BanSystem.getBanmanager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender.hasPermission("bansys.unban")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length < 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
								BanSystem.messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", BanSystem.PREFIX)));
						return false;
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
							for (Player all : Bukkit.getOnlinePlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											BanSystem.messages.getString("Unban.needreason.notify")
													.replaceAll("%P%", BanSystem.PREFIX)
													.replaceAll("%player%", UUIDFetcher.getName(id))
													.replaceAll("%sender%", sender.getName())
													.replaceAll("%reason%", reason)));
								}
							}
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unban.needreason.notify")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%sender%", sender.getName())
									.replaceAll("%reason%", reason)));
							
							if(sender instanceof Player) {
								Player p = (Player) sender;
								bm.unban(id, bm.getID(id, Type.NETWORK), p.getUniqueId() , reason);
							} else
								bm.unban(id, bm.getID(id, Type.NETWORK), sender.getName(), reason);
							
						} else {
							
							sender.sendMessage(BanSystem.messages.getString("Unban.success")
									.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id)));
							for (Player all : Bukkit.getOnlinePlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
											BanSystem.messages.getString("Unban.notify")
													.replaceAll("%P%", BanSystem.PREFIX)
													.replaceAll("%player%", UUIDFetcher.getName(id))
													.replaceAll("%sender%", sender.getName())
													.replaceAll("&", "§")));
								}
							}
							Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', 
									BanSystem.messages.getString("Unban.notify")
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%player%", UUIDFetcher.getName(id))
									.replaceAll("%sender%", sender.getName())));
							
							bm.unban(id);
							
						}
					} else {
						sender.sendMessage(
								BanSystem.messages.getString("Unban.notbanned").replaceAll("%P%", BanSystem.PREFIX)
										.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
					}
				} else if(BanSystem.config.getBoolean("needReason.Unban")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
							BanSystem.messages.getString("Unban.needreason.usage")
							.replaceAll("%P%", BanSystem.PREFIX)));
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', BanSystem.messages.getString("Unban.usage")
							.replaceAll("%P%", BanSystem.PREFIX)));
				}
			} else {
				sender.sendMessage(BanSystem.NODBCONNECTION);
			}
		} else {
			sender.sendMessage(BanSystem.NOPERMISSION);
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) {
		return TabCompleteUtil.completePlayerNames(sender, args);
	}
}