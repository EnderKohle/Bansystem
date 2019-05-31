package net.coalcube.bansystem.spigot.listener;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.URLUtils;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.Banmanager;
import net.coalcube.bansystem.spigot.util.UpdateChecker;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class LoginListener implements Listener {
	@EventHandler
	public void onLogin(PlayerJoinEvent e) {
		boolean isCancelled = false;
		if (BanSystem.mysql.isConnected()) {
			Banmanager bm = new Banmanager();
			Player p = e.getPlayer();
			if (bm.isBannedNetwork(p.getUniqueId())) {
				if (bm.getEnd(p.getUniqueId(), bm.getReasonNetwork(p.getUniqueId())) > System.currentTimeMillis()
						|| bm.getEnd(p.getUniqueId(), bm.getReasonNetwork(p.getUniqueId())) == -1) {

					String component = BanSystem.Banscreen.replaceAll("%Reason%", bm.getReasonNetwork(p.getUniqueId()))
							.replaceAll("%ReamingTime%",
									bm.getRemainingTime(p.getUniqueId(), bm.getReasonNetwork(p.getUniqueId())))
							.replaceAll("&", "§");
					p.kickPlayer(component);
					e.setJoinMessage("");
					isCancelled = true;
					if (bm.needIP(p.getUniqueId())) {
						bm.setIP(p.getUniqueId(), p.getAddress().getAddress());
					}
				} else {
					bm.unban(p.getUniqueId());
					Bukkit.getConsoleSender()
							.sendMessage(BanSystem.messages.getString("Ban.Network.autounban")
									.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", p.getName())
									.replaceAll("&", "§"));
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.hasPermission("bansys.notify")) {
							all.sendMessage(BanSystem.messages.getString("Ban.Network.autounban")
									.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", p.getName())
									.replaceAll("&", "§"));
						}
					}
				}
			}
			if (!isCancelled) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(BanSystem.plugin, new Runnable() {

					@Override
					public void run() {
						if (BanSystem.config.getBoolean("VPN.enable")) {
							if (URLUtils.isVPN(p.getAddress().getAddress().toString().replaceAll("/", ""))) {
								if (BanSystem.config.getBoolean("VPN.autoban.enable")) {
									bm.ban(p.getUniqueId(), BanSystem.config.getInt("VPN.autoban.ID"), "CONSOLE",
											p.getAddress().getAddress());
								} else {
									for (Player all : Bukkit.getOnlinePlayers()) {
										all.sendMessage(BanSystem.messages.getString("VPN.warning")
												.replaceAll("%P%", BanSystem.PREFIX)
												.replaceAll("%player%", p.getDisplayName()).replaceAll("&", "§"));
									}
								}
							}
						}

						if (p.hasPermission("bansys.ban.admin")) {
							try {
								if (new UpdateChecker(BanSystem.plugin, 65863).checkForUpdates()) {

									p.sendMessage(BanSystem.PREFIX + "§cEin neues Update ist verfügbar.");

									TextComponent comp = new TextComponent();
									comp.setText(BanSystem.PREFIX
											+ "§7Lade es dir unter §ehttps://www.spigotmc.org/resources/bansystem-mit-ids.65863/ §7runter um aktuell zu bleiben.");
									comp.setClickEvent(new ClickEvent(Action.OPEN_URL,
											"https://www.spigotmc.org/resources/bansystem-mit-ids.65863/"));
									comp.setHoverEvent(new HoverEvent(
											net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder("Klicke um zur Webseite zu gelangen").create()));

									p.spigot().sendMessage(comp);
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						}

						if (bm.getIPs().contains(p.getAddress().getAddress())) {
							String names = "";
							boolean rightType = false;
							ArrayList<UUID> banned = bm.getBannedPlayers(p.getAddress().getAddress());

							for (UUID id : banned) {
								if (bm.getType(id, bm.getReasonNetwork(id)) == Type.NETWORK)
									rightType = true;
								if (names.length() == 0) {
									names = UUIDFetcher.getName(id);
								} else {
									names = (names + ", " + UUIDFetcher.getName(id));
								}
							}
							if (rightType) {
								if (BanSystem.config.getBoolean("IPautoban.enable")) {
									new Banmanager().ban(p.getUniqueId(), BanSystem.config.getInt("IPautoban.banid"),
											"CONSOLE", p.getAddress().getAddress());
									Bukkit.getConsoleSender().sendMessage(BanSystem.PREFIX + "§cDer 2. Account von §e"
											+ names + " §cwurde automatisch gebannt für §e"
											+ BanSystem.config.getString(
													"IDs." + BanSystem.config.getInt("IPautoban.banid") + ".reason")
											+ "§c.");
									for (Player all : Bukkit.getOnlinePlayers()) {
										if (all.hasPermission("bansys.notify")) {
											all.sendMessage(BanSystem.PREFIX + "§cDer 2. Account von §e" + names
													+ " §cwurde automatisch gebannt für §e"
													+ BanSystem.config.getString("IDs."
															+ BanSystem.config.getInt("IPautoban.banid") + ".reason")
													+ "§c.");
										}
									}
									String component = BanSystem.Banscreen
											.replaceAll("%Reason%", bm.getReasonNetwork(p.getUniqueId()))
											.replaceAll("%ReamingTime%", bm.getRemainingTime(p.getUniqueId(),
													bm.getReasonNetwork(p.getUniqueId())));
									p.kickPlayer(component);
								} else {
									Bukkit.getConsoleSender().sendMessage(BanSystem.PREFIX + "§e" + p.getName()
											+ " §cist womöglich ein 2. Account von §e" + names);
									for (Player all : Bukkit.getOnlinePlayers()) {
										if (all.hasPermission("bansys.notify")) {
											all.sendMessage(BanSystem.PREFIX + "§e" + p.getName()
													+ " §cist womöglich ein 2. Account von §e" + names);
										}
									}
								}
							}
						}
					}
				}, 20 * 1);
			}
		}
	}
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (new Banmanager().isBannedNetwork(p.getUniqueId())) {
			e.setQuitMessage("");
		}
	}
}
