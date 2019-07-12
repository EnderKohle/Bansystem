package net.coalcube.bansystem.core.command;

import java.util.UUID;

import net.coalcube.bansystem.core.BanSystem;
import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.MySQL;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.User;

public class CMDdeletehistory implements Command {

	private Banmanager bm;
	private Config messages;
	private MySQL mysql;

	public CMDdeletehistory(Banmanager banmanager, Config messages, MySQL mysql) {
		this.bm = banmanager;
		this.messages = messages;
		this.mysql = mysql;
	}

	@Override
	public void execute(User sender, String[] args) {
		if (sender.hasPermission("bansys.history.delete")) {
			if (mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
						return;
					}
					if (bm.hashistory(id)) {
						bm.clearHistory(id);
						sender.sendMessage(messages.getString("Deletehistory.success")
								.replaceAll("%P%", messages.getString("prefix"))
								.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
						for (User all : BanSystem.getInstance().getAllPlayers()) {
							if (all.hasPermission("bansys.notify") && all != sender) {
								all.sendMessage(messages.getString("Deletehistory.notify")
										.replaceAll("%P%", messages.getString("prefix"))
										.replaceAll("%player%", UUIDFetcher.getName(id))
										.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
							}
						}
						BanSystem.getInstance().getConsole()
								.sendMessage(messages.getString("Deletehistory.notify")
										.replaceAll("%P%", messages.getString("prefix"))
										.replaceAll("%player%", UUIDFetcher.getName(id))
										.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
					} else {
						sender.sendMessage(messages.getString("History.historynotfound")
								.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(messages.getString("Deletehistory.usage")
							.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
				}
			} else {
				sender.sendMessage(messages.getString("NoDBConnection"));
			}
		} else {
			sender.sendMessage(messages.getString("NoPermission"));
		}
	}
}
