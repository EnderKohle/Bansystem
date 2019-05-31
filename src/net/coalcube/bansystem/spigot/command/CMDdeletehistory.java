package net.coalcube.bansystem.spigot.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.Banmanager;
import net.coalcube.bansystem.spigot.util.TabCompleteUtil;

public class CMDdeletehistory implements CommandExecutor, TabExecutor {

	Banmanager bm = new Banmanager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender.hasPermission("bansys.history.delete")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(BanSystem.messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						return false;
					}
					if (bm.hashistory2(id)) {
						bm.clearHistory(id);
						sender.sendMessage(BanSystem.messages.getString("Deletehistory.success")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id))
								.replaceAll("&", "§"));
						for (Player all : Bukkit.getOnlinePlayers()) {
							if (all.hasPermission("bansys.notify") && all != sender) {
								all.sendMessage(BanSystem.messages.getString("Deletehistory.notify")
										.replaceAll("%P%", BanSystem.PREFIX)
										.replaceAll("%player%", UUIDFetcher.getName(id))
										.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
							}
						}
						Bukkit.getConsoleSender().sendMessage(BanSystem.messages.getString("Deletehistory.notify")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id))
								.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
						return true;
					} else {
						sender.sendMessage(BanSystem.messages.getString("History.historynotfound")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(BanSystem.messages.getString("Deletehistory.usage")
							.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
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
