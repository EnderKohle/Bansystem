package net.coalcube.bansystem.core.command;

import java.util.UUID;

import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.User;
import net.coalcube.bansystem.core.BanSystem;
import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.MySQL;
import net.coalcube.bansystem.core.util.Type;

public class CMDunmute implements Command {

	private Banmanager bm;
	private Config messages;
	private Config config;
	private MySQL mysql;

	public CMDunmute(Banmanager banmanager, Config messages, Config config, MySQL mysql) {
		this.bm = banmanager;
	}

	@Override
	public void execute(User sender, String[] args) {
		if (sender.hasPermission("bansys.unmute")) {
			if (mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(messages.getString("Playerdoesnotexist").replaceAll("%P%",
								messages.getString("prefix")));
						return;
					}
					if (bm.isBannedChat(id)) {
						if (args.length > 1 && config.getBoolean("needReason.Unmute")) {

							String reason = "";
							for (int i = 1; i < args.length; i++) {
								reason = reason + args[i] + " ";
							}

							sender.sendMessage(messages.getString("Unmute.needreason.success")
									.replaceAll("%P%", messages.getString("prefix"))
									.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason));
							for (User all : BanSystem.getInstance().getAllPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(messages.getString("Unmute.needreason.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()).replaceAll("%reason%", reason));
								}
							}
							BanSystem.getInstance().getConsole()
									.sendMessage(messages.getString("Unmute.needreason.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()).replaceAll("%reason%", reason));

							if (sender.getUniqueId() != null) {
								bm.unmute(id, bm.getID(id, Type.CHAT), sender.getUniqueId(), reason);
							} else
								bm.unmute(id, bm.getID(id, Type.CHAT), sender.getName(), reason);

						} else {

							sender.sendMessage(
									messages.getString("Unmute.success").replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id)));
							for (User all : BanSystem.getInstance().getAllPlayers()) {
								if (all.hasPermission("bansys.notify") && all != sender) {
									all.sendMessage(messages.getString("Unmute.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()).replaceAll("&", "§"));
								}
							}
							BanSystem.getInstance().getConsole()
									.sendMessage(messages.getString("Unmute.notify")
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%player%", UUIDFetcher.getName(id))
											.replaceAll("%sender%", sender.getName()));

							bm.unmute(id);

						}
					} else {
						sender.sendMessage(
								messages.getString("Unmute.notmuted").replaceAll("%P%", messages.getString("prefix"))
										.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(messages.getString("Unmute.usage")
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
