package net.coalcube.bansystem.bungee.command;

import java.util.UUID;

import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.bungee.util.Banmanager;
import net.coalcube.bansystem.bungee.util.TabCompleteUtil;
import net.coalcube.bansystem.bungee.util.Type;
import net.coalcube.bansystem.bungee.util.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CMDcheck extends Command implements TabExecutor {

	public CMDcheck(String name) {
		super(name);
	}

	Banmanager bm = new Banmanager();

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("bansys.check")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(
								BanSystem.messages.getString("Playerdoesnotexist").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						return;
					}

					if (bm.isBannedChat(id) && bm.isBannedNetwork(id)) {

						sender.sendMessage(
								BanSystem.PREFIX + "§8§m------§8» §e" + UUIDFetcher.getName(id) + " §8«§m------");
						sender.sendMessage(BanSystem.PREFIX + "§7Von §8» §c" + bm.getBanner(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Grund §8» §c" + bm.getReasonChat(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Verbleibende Zeit §8» §c"
								+ bm.getRemainingTime(id, bm.getReasonChat(id)));
						sender.sendMessage(BanSystem.PREFIX + "§7Type §8» §c" + Type.CHAT);
						sender.sendMessage(BanSystem.PREFIX + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonChat(id)));
						sender.sendMessage(BanSystem.PREFIX);
						sender.sendMessage(BanSystem.PREFIX + "§7Von §8» §c" + bm.getBanner(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Grund §8» §c" + bm.getReasonNetwork(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Verbleibende Zeit §8» §c"
								+ bm.getRemainingTime(id, bm.getReasonNetwork(id)));
						sender.sendMessage(BanSystem.PREFIX + "§7Type §8» §c" + Type.NETWORK);
						sender.sendMessage(
								BanSystem.PREFIX + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonNetwork(id)));

					} else if (bm.isBannedChat(id)) {

						sender.sendMessage(
								BanSystem.PREFIX + "§8§m------§8» §e" + UUIDFetcher.getName(id) + " §8«§m------");
						sender.sendMessage(BanSystem.PREFIX + "§7Von §8» §c" + bm.getBanner(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Grund §8» §c" + bm.getReasonChat(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Verbleibende Zeit §8» §c"
								+ bm.getRemainingTime(id, bm.getReasonChat(id)));
						sender.sendMessage(BanSystem.PREFIX + "§7Type §8» §c" + Type.CHAT);
						sender.sendMessage(BanSystem.PREFIX + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonChat(id)));

					} else if (bm.isBannedNetwork(id)) {
						sender.sendMessage(
								BanSystem.PREFIX + "§8§m------§8» §e" + UUIDFetcher.getName(id) + " §8«§m------");
						sender.sendMessage(BanSystem.PREFIX + "§7Von §8» §c" + bm.getBanner(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Grund §8» §c" + bm.getReasonNetwork(id));
						sender.sendMessage(BanSystem.PREFIX + "§7Verbleibende Zeit §8» §c"
								+ bm.getRemainingTime(id, bm.getReasonNetwork(id)));
						sender.sendMessage(BanSystem.PREFIX + "§7Type §8» §c" + Type.NETWORK);
						sender.sendMessage(
								BanSystem.PREFIX + "§7Level §8» §c" + bm.getLevel(id, bm.getReasonNetwork(id)));
					} else {
						sender.sendMessage(BanSystem.messages.getString("Playernotbanned")
								.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
					}
				} else {
					sender.sendMessage(BanSystem.messages.getString("Check.usage").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
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
