package net.coalcube.bansystem.core.command;

import java.util.UUID;

import net.coalcube.bansystem.core.BanSystem;
import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.MySQL;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.User;

public class CMDunban implements Command {

	private Banmanager bm;
	private MySQL mysql;
	private Config messages;
	private Config config;

	public CMDunban(Banmanager banmanager, MySQL mysql, Config messages, Config config) {
		this.bm = banmanager;
		this.mysql = mysql;
		this.messages = messages;
		this.config = config;
	}

	@Override
	public void execute(User sender, String[] args) {
		if (sender.hasPermission("bansys.unban")) {
			if (mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
						return;
					}
					if (bm.isBannedNetwork(id)) {
						if (args.length > 1 && config.getBoolean("needReason.Unban")) {

							String reason = "";
							for (int i = 1; i < args.length; i++) {
								reason = reason + args[i] + " ";
							}

							sender.sendMessage(messages.getString("Unban.needreason.success")
									.replaceAll("%P%", messages.getString("prefix"))
									.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason));
							for (User all : BanSystem.getInstance().getAllPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(messages.getString("Unban.needreason.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()).replaceAll("%reason%", reason));
								}
							}
							BanSystem.getInstance().getConsole()
									.sendMessage(messages.getString("Unban.needreason.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()).replaceAll("%reason%", reason));

							if (sender.getUniqueId() != null) {
								bm.unban(id, bm.getID(id, Type.NETWORK), sender.getUniqueId(), reason);
							} else
								bm.unban(id, bm.getID(id, Type.NETWORK), sender.getName(), reason);

						} else {

							sender.sendMessage(
									messages.getString("Unban.success").replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id)));
							for (User all : BanSystem.getInstance().getAllPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(messages.getString("Unban.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
								}
							}
							BanSystem.getInstance().getConsole()
									.sendMessage(messages.getString("Unban.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()));

							bm.unban(id);

						}
					} else {
						sender.sendMessage(
								messages.getString("Unban.notbanned").replaceAll("%P%", messages.getString("prefix"))
										.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(messages.getString("Unban.usage").replaceAll("%P%", messages.getString("prefix"))
							.replaceAll("&", "§"));
				}
			} else {
				sender.sendMessage(messages.getString("NoDBConnection"));
			}
		} else {
			sender.sendMessage(messages.getString("NoPermission"));
		}
	}
}
