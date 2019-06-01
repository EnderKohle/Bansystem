package net.coalcube.bansystem.bungee.command;

import java.util.UUID;

import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.bungee.util.Banmanager;
import net.coalcube.bansystem.bungee.util.TabCompleteUtil;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CMDban extends Command implements TabExecutor {

	private Banmanager banmanager = new Banmanager();

	public CMDban(String name) {
		super(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
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
						return;
					}
				}

				if (args.length == 2) {
					if (!BanSystem.config.getSection("IDs").getKeys().contains(args[1])) {
						sender.sendMessage(
								BanSystem.messages.getString("Ban.invalidinput").replaceAll("%P%", BanSystem.PREFIX).replaceAll("&", "§"));
						reason = null;
						return;
					}
					for (String key : BanSystem.config.getSection("IDs").getKeys()) {
						if (args[1].equalsIgnoreCase(key)) {
							if (BanSystem.config.getBoolean("IDs." + key + ".onlyAdmins")) {
								if (!sender.hasPermission("bansys.ban.admin")) {
									sender.sendMessage(BanSystem.messages.getString("Ban.onlyadmins").replaceAll("%P%",
											BanSystem.PREFIX).replaceAll("&", "§"));
									return;
								}
							}
							reason = BanSystem.config.getString("IDs." + key + ".reason");
							type = Type.valueOf(BanSystem.config.getString("IDs." + key + ".type"));
							if (banmanager.hashistory(id, reason)) {
								lvl = (byte) (banmanager.getLevel(id, reason) + 1);
							} else {
								lvl = 1;
							}
							for (String lvlkey : BanSystem.config.getSection("IDs." + key + ".lvl").getKeys()) {
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
					for (String key : BanSystem.config.getSection("IDs").getKeys()) {
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
	}

	@SuppressWarnings({ "deprecation" })
	private void ban(CommandSender sender, UUID id, Type type, long dauer, String reason) {
		String ersteller = sender.getName();
		if ((type == Type.CHAT && !banmanager.isBannedChat(id)
				|| (type == Type.NETWORK && !banmanager.isBannedNetwork(id)))) {
			if (ProxyServer.getInstance().getPlayer(id) != null) {
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(id);
				if (target.hasPermission("bansys.ban") && !sender.hasPermission("bansys.ban.admin")) {
					sender.sendMessage(BanSystem.messages.getString("Ban.cannotbanteammembers").replaceAll("%P%",
								BanSystem.PREFIX).replaceAll("&", "§"));
					return;
				} else {
					banmanager.ban(reason, dauer, ersteller, type, target.getUniqueId(),
							target.getAddress().getAddress());
				}
				if (type == net.coalcube.bansystem.core.util.Type.NETWORK) {
					target.disconnect(
							BanSystem.Banscreen
							.replaceAll("%Reason%", banmanager.getReasonNetwork(id))
							.replaceAll("%ReamingTime%", banmanager.getRemainingTime(id, banmanager.getReasonNetwork(id)))
							.replaceAll("&", "§"));
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
				ProxyServer.getInstance().getConsole()
						.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX)
								.replaceAll("%player%", UUIDFetcher.getName(id)).replaceAll("%reason%", reason)
								.replaceAll("%reamingTime%", banmanager.getRemainingTime(id, reason))
								.replaceAll("%banner%", ersteller).replaceAll("%type%", type.toString())
								.replaceAll("&", "§"));
			}
			for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
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
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return TabCompleteUtil.completePlayerNames(sender, args);
	}
}
