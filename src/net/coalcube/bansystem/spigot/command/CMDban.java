package net.coalcube.bansystem.spigot.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.Banmanager;
import net.coalcube.bansystem.spigot.util.TabCompleteUtil;

public class CMDban implements CommandExecutor, TabExecutor {

	private Banmanager banmanager = new Banmanager();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		Type type = null;
		String reason = null;
		byte lvl;
		long dauer = 0;
		UUID id = null;
		if (sender.hasPermission("bansys.ban")) {
			if (BanSystem.mysql.isConnected()) {
				if (args.length >= 1) {
					id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(
								BanSystem.messages.getString("Playerdoesnotexist").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						return false;
					}
				}

				if (args.length == 2) {
					if (!BanSystem.config.getConfigurationSection("IDs").getKeys(false).contains(args[1])) {
						sender.sendMessage(
								BanSystem.messages.getString("Ban.invalidinput").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						reason = null;
						return false;
					}
					for (String key : BanSystem.config.getStringList("IDs")) {
						if (args[1].equalsIgnoreCase(key)) {
							if (BanSystem.config.getBoolean("IDs." + key + ".onlyAdmins")) {
								if (!sender.hasPermission("bansys.ban.admin")) {
									sender.sendMessage(BanSystem.messages.getString("Ban.onlyadmins").replaceAll("%P%",
											BanSystem.PREFIX).replaceAll("&", "§"));
									return false;
								}
							}
							reason = BanSystem.config.getString("IDs." + key + ".reason");
							type = Type.valueOf(BanSystem.config.getString("IDs." + key + ".type"));
							if (banmanager.hashistory(id, reason)) {
								lvl = (byte) (banmanager.getLevel(id, reason) + 1);
							} else {
								lvl = 1;
							}
							for (String lvlkey : BanSystem.config.getConfigurationSection("IDs." + key + ".lvl").getKeys(false)) {
								if ((byte) Byte.valueOf(lvlkey) == lvl) {
									dauer = BanSystem.config.getLong("IDs." + key + ".lvl." + lvlkey + ".duration");
								}
							}
							lvl = (byte) (lvl + 1);
							if (dauer == 0) {
								dauer = -1;
							}
						}
					}

					if (id != null && type != null) {
						if(sender.hasPermission("bansys.ban."+id) || sender.hasPermission("bansys.ban.all") || sender.hasPermission("bansys.ban.admin")) {
							ban(sender, id, type, dauer, reason);
						} else sender.sendMessage(BanSystem.messages.getString("Ban.ID.NoPermission").replaceAll("%P%", BanSystem.PREFIX));
						
					}
				} else {
					sender.sendMessage(BanSystem.messages.getString("Ban.ID.Listlayout.heading").replaceAll("%P%", BanSystem.PREFIX));
					for (String key : BanSystem.config.getConfigurationSection("IDs").getKeys(false)) {
						if(BanSystem.config.getBoolean("IDs." + key + ".onlyAdmins")) {
							sender.sendMessage(BanSystem.messages.getString("Ban.ID.Listlayout.IDs.onlyadmins")
									.replaceAll("%ID%", key)
									.replaceAll("%reason%", BanSystem.config.getString("IDs." + key + ".reason"))
									.replaceAll("%P%", BanSystem.PREFIX)
									.replaceAll("%type%", BanSystem.config.getString("IDs." + key + ".type"))
									.replaceAll("&", "§"));
						} else 
							sender.sendMessage(BanSystem.messages.getString("Ban.ID.Listlayout.IDs.general")
								.replaceAll("%ID%", key)
								.replaceAll("%reason%", BanSystem.config.getString("IDs." + key + ".reason"))
								.replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("%type%", BanSystem.config.getString("IDs." + key + ".type"))
								.replaceAll("&", "§"));
					}
					sender.sendMessage(BanSystem.messages.getString("Ban.usage").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
				}
			} else {
				sender.sendMessage(BanSystem.NODBCONNECTION);
			}
		} else {
			sender.sendMessage(BanSystem.NOPERMISSION);
		}
		return false;
	}
	
	private void ban(CommandSender sender, UUID id, Type type, long dauer, String reason) {
		String ersteller = sender.getName();
		if ((type == Type.CHAT && !banmanager.isBannedChat(id)
				|| (type == Type.NETWORK && !banmanager.isBannedNetwork(id)))) {
			if (Bukkit.getPlayer(id) != null) {
				Player target = Bukkit.getPlayer(id);
				if (target.hasPermission("bansys.ban") && !sender.hasPermission("bansys.ban.admin")) {
					sender.sendMessage(BanSystem.messages.getString("Ban.cannotbanteammembers").replaceAll("%P%",
								BanSystem.PREFIX).replaceAll("&", "§"));
					return;
				} else { // sowas bitte in ne core package also so: heilige scheiße ... du hast ernsthaft alles 2x? auch das das garnicht software abhängig ist? :( ich bin enttäuscht xD  maxchD das core package!
					banmanager.ban(reason, dauer, ersteller, type, target.getUniqueId(), // warte manche sachen sind software abhängig ja  die musst du dann extra machen aber ehrlich gesagt würde ich da eigentlich mit maven modulen: bungee core, spigot arbeiten weil dein problem ist z.b bungee hat ne andere version von google guava als spigot und dann nimmt eclipse natürlich das neuere von bungee aber wenn du es dann auf spigot ausführst gehts auf einmal net und du fragst dich dann häää eclipse zeigt doch keinen fehler an aber da steht dann sowas wie MethodNotFoundException xD okay xD hatte ich mal also man sollte das eig. aufspalten aber du hast vermutlich bis jz sowieso nix bei dem das zutrifft joa
							target.getAddress().getAddress());
				}
				if (type == Type.NETWORK) { // der fully qualified name ist unnötig xD das war doch mal weil du die variable Type genannt hattest oder? mit großem t xD
					target.kickPlayer(
							BanSystem.Banscreen.replaceAll("%Reason%", banmanager.getReasonNetwork(id)).replaceAll(
									"%ReamingTime%", banmanager.getRemainingTime(id, banmanager.getReasonNetwork(id))).replaceAll("&", "§"));
				} else {
					for (String message : BanSystem.messages.getStringList("Ban.Chat.Screen")) {
						target.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%reason%", reason)
								.replaceAll("%reamingtime%", banmanager.getRemainingTime(id, reason)).replaceAll("&", "§"));
					}
				}
				sender.sendMessage(BanSystem.messages.getString("Ban.success").replaceAll("%P%", BanSystem.PREFIX)
						.replaceAll("%Player%", target.getDisplayName()).replaceAll("%reason%", reason).replaceAll("&", "§"));
			} else {
				banmanager.ban(reason, dauer, ersteller, type, id, null);
				sender.sendMessage(BanSystem.messages.getString("Ban.success").replaceAll("%P%", BanSystem.PREFIX)
						.replaceAll("%Player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason).replaceAll("&", "§"));
			}
			for (String message : BanSystem.messages.getStringList("Ban.notify")) {
				Bukkit.getConsoleSender()
						.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason)
								.replaceAll("%reamingTime%", banmanager.getRemainingTime(id, reason))
								.replaceAll("%banner%", ersteller).replaceAll("%type%", type.toString())
								.replaceAll("&", "§"));
			}
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (all.hasPermission("bansys.notify") && (all != sender)) {
					for (String message : BanSystem.messages.getStringList("Ban.notify")) {
						all.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason)
								.replaceAll("%reamingTime%", banmanager.getRemainingTime(id, reason))
								.replaceAll("%banner%", ersteller).replaceAll("%type%", type.toString())
								.replaceAll("&", "§"));
					}
				}
			}
		} else {
			sender.sendMessage(BanSystem.messages.getString("Ban.alreadybanned").replaceAll("%P%", BanSystem.PREFIX)
					.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String lable, String[] args) {
		return TabCompleteUtil.completePlayerNames(sender, args);
	}
}
