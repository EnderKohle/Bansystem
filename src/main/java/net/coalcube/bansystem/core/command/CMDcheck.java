package net.coalcube.bansystem.core.command;

import java.util.UUID;

import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.MySQL;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.User;

public class CMDcheck implements Command {

	private Banmanager bm;
	private MySQL mysql;
	private Config messages;
	
	public CMDcheck(Banmanager banmanager, MySQL mysql, Config messages) {
		this.bm = banmanager;
		this.mysql = mysql;
		this.messages = messages;
	}

	@Override
	public void execute(User sender, String[] args) {
		if (sender.hasPermission("bansys.check")) {
			if (mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(
								messages.getString("Playerdoesnotexist").replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
						return;
					}

					if (bm.isBannedChat(id) && bm.isBannedNetwork(id)) {

						sender.sendMessage(messages.getString("prefix") + "§8§m------§8» §e" + UUIDFetcher.getName(id) + " §8«§m------");
						sender.sendMessage(messages.getString("prefix") + "§7Von §8» §c" + bm.getBanner(id, Type.CHAT));
						sender.sendMessage(messages.getString("prefix") + "§7Grund §8» §c" + bm.getReasonChat(id));
						sender.sendMessage(messages.getString("prefix") + "§7Verbleibende Zeit §8» §c" + bm.getRemainingTime(id, bm.getReasonChat(id)));
						sender.sendMessage(messages.getString("prefix") + "§7Type §8» §c" + Type.CHAT);
						sender.sendMessage(messages.getString("prefix") + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonChat(id)));
						sender.sendMessage(messages.getString("prefix"));
						sender.sendMessage(messages.getString("prefix") + "§7Von §8» §c" + bm.getBanner(id, Type.NETWORK));
						sender.sendMessage(messages.getString("prefix") + "§7Grund §8» §c" + bm.getReasonNetwork(id));
						sender.sendMessage(messages.getString("prefix") + "§7Verbleibende Zeit §8» §c" + bm.getRemainingTime(id, bm.getReasonNetwork(id)));
						sender.sendMessage(messages.getString("prefix") + "§7Type §8» §c" + Type.NETWORK);
						sender.sendMessage(messages.getString("prefix") + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonNetwork(id)));
						sender.sendMessage(messages.getString("prefix") + "§8§m-----------------");

					} else if (bm.isBannedChat(id)) {

						sender.sendMessage(messages.getString("prefix") + "§8§m------§8» §e" + UUIDFetcher.getName(id) + " §8«§m------");
						sender.sendMessage(messages.getString("prefix") + "§7Von §8» §c" + bm.getBanner(id, Type.CHAT));
						sender.sendMessage(messages.getString("prefix") + "§7Grund §8» §c" + bm.getReasonChat(id));
						sender.sendMessage(messages.getString("prefix") + "§7Verbleibende Zeit §8» §c" + bm.getRemainingTime(id, bm.getReasonChat(id)));
						sender.sendMessage(messages.getString("prefix") + "§7Type §8» §c" + Type.CHAT);
						sender.sendMessage(messages.getString("prefix") + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonChat(id)));
						sender.sendMessage(messages.getString("prefix") + "§8§m-----------------");
						
					} else if (bm.isBannedNetwork(id)) {
						sender.sendMessage(messages.getString("prefix") + "§8§m------§8» §e" + UUIDFetcher.getName(id) + " §8«§m------");
						sender.sendMessage(messages.getString("prefix") + "§7Von §8» §c" + bm.getBanner(id, Type.NETWORK));
						sender.sendMessage(messages.getString("prefix") + "§7Grund §8» §c" + bm.getReasonNetwork(id));
						sender.sendMessage(messages.getString("prefix") + "§7Verbleibende Zeit §8» §c" + bm.getRemainingTime(id, bm.getReasonNetwork(id)));
						sender.sendMessage(messages.getString("prefix") + "§7Type §8» §c" + Type.NETWORK);
						sender.sendMessage(messages.getString("prefix") + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonNetwork(id)));
						sender.sendMessage(messages.getString("prefix") + "§8§m-----------------");
					} else {
						sender.sendMessage(messages.getString("Playernotbanned")
								.replaceAll("%P%", messages.getString("prefix"))
								.replaceAll("%player%", UUIDFetcher.getName(id))
								.replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(messages.getString("Check.usage")
							.replaceAll("%P%", messages.getString("prefix"))
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