package net.coalcube.bansystem.spigot.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.Banmanager;
import net.coalcube.bansystem.spigot.util.TabCompleteUtil;
import net.coalcube.bansystem.spigot.util.UUIDFetcher;

public class CMDunban implements CommandExecutor, TabExecutor {

	Banmanager bm = new Banmanager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender.hasPermission("bansys.unban")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(BanSystem.messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						return false;
					}
					if (bm.isBannedNetwork(id)) {
						bm.unban(id);
						sender.sendMessage(BanSystem.messages.getString("Unban.success")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id)));
						for (Player all : Bukkit.getOnlinePlayers()) {
							if (all.hasPermission("bansys.notify") && all != sender) {
								all.sendMessage(
										BanSystem.messages.getString("Unban.notify").replaceAll("%P%", BanSystem.PREFIX)
												.replaceAll("%player%", UUIDFetcher.getName(id))
												.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
							}
						}
						Bukkit.getConsoleSender().sendMessage(BanSystem.messages.getString("Unban.notify")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id))
								.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
					} else {
						sender.sendMessage(
								BanSystem.messages.getString("Unban.notbanned").replaceAll("%P%", BanSystem.PREFIX)
										.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(BanSystem.messages.getString("Unban.usage").replaceAll("%P%", BanSystem.PREFIX)
							.replaceAll("&", "§"));
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
