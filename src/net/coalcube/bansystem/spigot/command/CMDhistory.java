package net.coalcube.bansystem.spigot.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.Banmanager;
import net.coalcube.bansystem.spigot.util.TabCompleteUtil;
import net.coalcube.bansystem.core.util.UUIDFetcher;

public class CMDhistory implements CommandExecutor, TabExecutor {

	private static Banmanager bm = BanSystem.getBanmanager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender.hasPermission("bansys.history.show")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(BanSystem.messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						return false;
					}
					if (bm.hashistory(id)) {
						bm.sendHistorys(id, sender);
						return true;
					} else {
						sender.sendMessage(BanSystem.messages.getString("History.historynotfound")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(BanSystem.messages.getString("History.usage").replaceAll("%P%", BanSystem.PREFIX)
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