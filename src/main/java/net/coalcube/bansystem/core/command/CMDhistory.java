package net.coalcube.bansystem.core.command;

import java.util.UUID;

import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.MySQL;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.User;

public class CMDhistory implements Command {

	private Banmanager bm;
	private Config messages;
	private MySQL mysql;

	public CMDhistory(Banmanager banmanager, Config messages, MySQL mysql) {
		this.bm = banmanager;
		this.messages = messages;
		this.mysql = mysql;
	}

	@Override
	public void execute(User sender, String[] args) {
		if (sender.hasPermission("bansys.history.show")) {
			if (mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
						return;
					}
					if (bm.hashistory(id)) {
						bm.sendHistorys(id, sender);
					} else {
						sender.sendMessage(messages.getString("History.historynotfound")
								.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(messages.getString("History.usage")
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
