package net.coalcube.bansystem.core.command;

import java.util.UUID;

import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.MySQL;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CMDban implements Command {

	private Banmanager banmanager;
	private Config config;
	private Config messages;
	private MySQL mysql;

	public CMDban(Banmanager banmanager, Config config, Config messages, MySQL mysql) {
		this.banmanager = banmanager;
		this.config = config;
		this.messages = messages;
		this.mysql = mysql;
	}

	@Override
	public void execute(User sender, String[] args) {
		Type type = null;
		String reason = null;
		byte lvl;
		long dauer = 0;
		UUID id = null;
		if (sender.hasPermission("bansys.ban")) {
			if (mysql.isConnected()) {
				if (args.length >= 1) {
					id = UUIDFetcher.getUUID(args[0]);
					if (id == null) {
						sender.sendMessage(messages.getString("Playerdoesnotexist")
								.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
						return;
					}
				}

				if (args.length == 2) {
					if (!config.getSection("IDs").getKeys().contains(args[1])) {
						sender.sendMessage(messages.getString("Ban.invalidinput")
								.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
						reason = null;
						return;
					}
					for (String key : config.getSection("IDs").getKeys()) {
						if (args[1].equalsIgnoreCase(key)) {
							if (config.getBoolean("IDs." + key + ".onlyAdmins")) {
								if (!sender.hasPermission("bansys.ban.admin")) {
									sender.sendMessage(messages.getString("Ban.onlyadmins")
											.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
									return;
								}
							}
							reason = config.getString("IDs." + key + ".reason");
							type = Type.valueOf(config.getString("IDs." + key + ".type"));
							if (banmanager.hashistory(id, reason)) {
								lvl = (byte) (banmanager.getLevel(id, reason) + 1);
							} else {
								lvl = 1;
							}
							for (String lvlkey : config.getSection("IDs." + key + ".lvl").getKeys()) {
								if ((byte) Byte.valueOf(lvlkey) == lvl) {
									dauer = config.getLong("IDs." + key + ".lvl." + lvlkey + ".duration");
								}
							}
							lvl = (byte) (lvl + 1);
							if (dauer == 0) {
								dauer = -1;
							}
						}
					}

					if (id != null && type != null) {
						if (sender.hasPermission("bansys.ban." + id) || sender.hasPermission("bansys.ban.all")
								|| sender.hasPermission("bansys.ban.admin")) {
							ban(sender, id, type, dauer, reason);
						} else
							sender.sendMessage(messages.getString("Ban.ID.NoPermission").replaceAll("%P%",
									messages.getString("prefix")));

					}
				} else {
					sender.sendMessage(messages.getString("Ban.ID.Listlayout.heading").replaceAll("%P%",
							messages.getString("prefix")));
					for (String key : config.getSection("IDs").getKeys()) {
						if (config.getBoolean("IDs." + key + ".onlyAdmins")) {
							sender.sendMessage(
									messages.getString("Ban.ID.Listlayout.IDs.onlyadmins").replaceAll("%ID%", key)
											.replaceAll("%reason%", config.getString("IDs." + key + ".reason"))
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%type%", config.getString("IDs." + key + ".type"))
											.replaceAll("&", "§"));
						} else
							sender.sendMessage(
									messages.getString("Ban.ID.Listlayout.IDs.general").replaceAll("%ID%", key)
											.replaceAll("%reason%", config.getString("IDs." + key + ".reason"))
											.replaceAll("%P%", messages.getString("prefix"))
											.replaceAll("%type%", config.getString("IDs." + key + ".type"))
											.replaceAll("&", "§"));
					}
					sender.sendMessage(messages.getString("Ban.usage").replaceAll("%P%", messages.getString("prefix"))
							.replaceAll("&", "§"));
				}
			} else {
				sender.sendMessage(messages.getString("NoDBConnection"));
			}
		} else {
			sender.sendMessage(messages.getString("NoPermission"));
		}
	}

	@SuppressWarnings({ "deprecation" })
	private void ban(User sender, UUID id, Type type, long dauer, String reason) {
		String ersteller = sender.getName();
		if ((type == Type.CHAT && !banmanager.isBannedChat(id)
				|| (type == Type.NETWORK && !banmanager.isBannedNetwork(id)))) {
			if (ProxyServer.getInstance().getPlayer(id) != null) {
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(id);
				if (target.hasPermission("bansys.ban") && !sender.hasPermission("bansys.ban.admin")) {
					sender.sendMessage(messages.getString("Ban.cannotbanteammembers")
							.replaceAll("%P%", messages.getString("prefix")).replaceAll("&", "§"));
					return;
				} else {
					banmanager.ban(reason, dauer, ersteller, type, target.getUniqueId(),
							target.getAddress().getAddress());
				}
				if (type == net.coalcube.bansystem.core.util.Type.NETWORK) {
					target.disconnect(
							messages.getString("BanScreen").replaceAll("%Reason%", banmanager.getReasonNetwork(id))
									.replaceAll("%ReamingTime%",
											banmanager.getRemainingTime(id, banmanager.getReasonNetwork(id)))
									.replaceAll("&", "§"));
				} else {
					for (String message : messages.getStringList("Ban.Chat.Screen")) {
						target.sendMessage(message.replaceAll("%P%", messages.getString("prefix"))
								.replaceAll("%reason%", reason).replaceAll("%Player%", target.getDisplayName())
								.replaceAll("%reason%", reason).replaceAll("&", "§"));
					}
				}
			} else {
				banmanager.ban(reason, dauer, ersteller, type, id, null);
				sender.sendMessage(messages.getString("Ban.success").replaceAll("%P%", messages.getString("prefix"))
						.replaceAll("%Player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason)
						.replaceAll("&", "§"));
			}
			for (String message : messages.getStringList("Ban.notify")) {
				ProxyServer.getInstance().getConsole()
						.sendMessage(message.replaceAll("%P%", messages.getString("prefix"))
								.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason)
								.replaceAll("%reamingTime%", banmanager.getRemainingTime(id, reason))
								.replaceAll("%banner%", ersteller).replaceAll("%type%", type.toString())
								.replaceAll("&", "§"));
			}
			for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
				if (all.hasPermission("bansys.notify") && (all != sender)) {
					for (String message : messages.getStringList("Ban.notify")) {
						all.sendMessage(message.replaceAll("%P%", messages.getString("prefix"))
								.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason)
								.replaceAll("%reamingTime%", banmanager.getRemainingTime(id, reason))
								.replaceAll("%banner%", ersteller).replaceAll("%type%", type.toString())
								.replaceAll("&", "§"));
					}
				}
			}
		} else {
			sender.sendMessage(messages.getString("Ban.alreadybanned").replaceAll("%P%", messages.getString("prefix"))
					.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("&", "§"));
		}
	}

}
