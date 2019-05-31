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
import net.coalcube.bansystem.spigot.util.Type;
import net.coalcube.bansystem.spigot.util.UUIDFetcher;

public class CMDcheck implements CommandExecutor, TabExecutor {

	Banmanager bm = new Banmanager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if (sender.hasPermission("bansys.check")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length == 1) {
					UUID id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(
								BanSystem.messages.getString("Playerdoesnotexist").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						return false;
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
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) {
		return TabCompleteUtil.completePlayerNames(sender, args);
	}
}
