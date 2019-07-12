package net.coalcube.bansystem.bungee.command;

import java.util.UUID;

import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.bungee.util.TabCompleteUtil;
import net.coalcube.bansystem.bungee.util.Banmanager;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CMDhistory extends Command implements TabExecutor {

	public CMDhistory(String name) {
		super(name);
	}

	private static Banmanager bm = BanSystem.getBanmanager();

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("bansys.history.show")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if(id == null) {
						sender.sendMessage(BanSystem.messages.getString("Playerdoesnotexist").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						return;
					}
					if (bm.hashistory(id)) {
						bm.sendHistorys(id, sender);
					} else {
						sender.sendMessage(BanSystem.messages.getString("History.historynotfound").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(BanSystem.messages.getString("History.usage").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
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
