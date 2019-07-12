package net.coalcube.bansystem.spigot.listener;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.URLUtils;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.UpdateChecker;
import net.coalcube.bansystem.spigot.BanSystemSpigot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("deprecation")
public class PlayerJoinListener implements Listener {
	
	private static Banmanager bm = BanSystemSpigot.getBanmanager();
	private static boolean banned;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLogin(PlayerPreLoginEvent e) {
		boolean isCancelled = false;
		if (BanSystemSpigot.mysql.isConnected()) {
			if (bm.isBannedNetwork(e.getUniqueId())) {
				if (bm.getEnd(e.getUniqueId(), bm.getReasonNetwork(e.getUniqueId())) > System.currentTimeMillis()
						|| bm.getEnd(e.getUniqueId(), bm.getReasonNetwork(e.getUniqueId())) == -1) {

					String component = BanSystemSpigot.Banscreen.replaceAll("%Reason%", bm.getReasonNetwork(e.getUniqueId()))
							.replaceAll("%ReamingTime%",
									bm.getRemainingTime(e.getUniqueId(), bm.getReasonNetwork(e.getUniqueId())))
							.replaceAll("&", "§");
					if(!BanSystemSpigot.config.getBoolean("Ban.KickDelay.enable")) e.disallow(Result.KICK_BANNED, component);
					banned = true;
					isCancelled = true;
					if (bm.needIP(e.getUniqueId())) {
						bm.setIP(e.getUniqueId(), e.getAddress());
					}
				} else {
					bm.unban(e.getUniqueId());
					Bukkit.getConsoleSender()
							.sendMessage(BanSystemSpigot.messages.getString("Ban.Network.autounban")
									.replaceAll("%P%", BanSystemSpigot.PREFIX).replaceAll("%player%", e.getName())
									.replaceAll("&", "§"));
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.hasPermission("bansys.notify")) {
							all.sendMessage(BanSystemSpigot.messages.getString("Ban.Network.autounban")
									.replaceAll("%P%", BanSystemSpigot.PREFIX).replaceAll("%player%", e.getName())
									.replaceAll("&", "§"));
						}
					}
				}
			}
			if (!isCancelled) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(BanSystemSpigot.getInstance(), new Runnable() {

					@Override
					public void run() {
						if (BanSystemSpigot.config.getBoolean("VPN.enable")) {
							if (URLUtils.isVPN(e.getAddress().getAddress().toString().replaceAll("/", ""))) {
								if (BanSystemSpigot.config.getBoolean("VPN.autoban.enable")) {
									bm.ban(e.getUniqueId(), BanSystemSpigot.config.getInt("VPN.autoban.ID"), "CONSOLE",
											e.getAddress());
								} else {
									for (Player all : Bukkit.getOnlinePlayers()) {
										all.sendMessage(BanSystemSpigot.messages.getString("VPN.warning")
												.replaceAll("%P%", BanSystemSpigot.PREFIX)
												.replaceAll("%player%", e.getName()).replaceAll("&", "§"));
									}
								}
							}
						}
						if (bm.getIPs().contains(e.getAddress())) {
							String names = "";
							boolean rightType = false;
							ArrayList<UUID> banned = bm.getBannedPlayers(e.getAddress());

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
								if (BanSystemSpigot.config.getBoolean("IPautoban.enable")) {
									new Banmanager().ban(e.getUniqueId(), BanSystemSpigot.config.getInt("IPautoban.banid"),
											"CONSOLE", e.getAddress());
									Bukkit.getConsoleSender().sendMessage(BanSystemSpigot.PREFIX + "§cDer 2. Account von §e"
											+ names + " §cwurde automatisch gebannt für §e"
											+ BanSystemSpigot.config.getString(
													"IDs." + BanSystemSpigot.config.getInt("IPautoban.banid") + ".reason")
											+ "§c.");
									for (Player all : Bukkit.getOnlinePlayers()) {
										if (all.hasPermission("bansys.notify")) {
											all.sendMessage(BanSystemSpigot.PREFIX + "§cDer 2. Account von §e" + names
													+ " §cwurde automatisch gebannt für §e"
													+ BanSystemSpigot.config.getString("IDs."
															+ BanSystemSpigot.config.getInt("IPautoban.banid") + ".reason")
													+ "§c.");
										}
									}
									String component = BanSystemSpigot.Banscreen
											.replaceAll("%Reason%", bm.getReasonNetwork(e.getUniqueId()))
											.replaceAll("%ReamingTime%", bm.getRemainingTime(e.getUniqueId(),
													bm.getReasonNetwork(e.getUniqueId())));
									if(!BanSystemSpigot.config.getBoolean("Ban.KickDelay.enable")) e.disallow(Result.KICK_BANNED, component);
									PlayerJoinListener.banned = true;
								} else {
									Bukkit.getConsoleSender().sendMessage(BanSystemSpigot.PREFIX + "§e" + e.getName()
											+ " §cist womöglich ein 2. Account von §e" + names);
									for (Player all : Bukkit.getOnlinePlayers()) {
										if (all.hasPermission("bansys.notify")) {
											all.sendMessage(BanSystemSpigot.PREFIX + "§e" + e.getName()
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
		if (bm.isBannedNetwork(p.getUniqueId())) {
			e.setQuitMessage(null);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("bansys.ban.admin")) {
			try {
				if (new UpdateChecker(65863).checkForUpdates()) {

					p.sendMessage(BanSystemSpigot.PREFIX + "§cEin neues Update ist verfügbar.");

					TextComponent comp = new TextComponent();
					comp.setText(BanSystemSpigot.PREFIX
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
		if(BanSystemSpigot.config.getBoolean("Ban.KickDelay.enable") && banned) {
			e.setJoinMessage(null);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					p.kickPlayer(BanSystemSpigot.Banscreen.replaceAll("%Reason%", bm.getReasonNetwork(p.getUniqueId()))
							.replaceAll("%ReamingTime%",
									bm.getRemainingTime(p.getUniqueId(), bm.getReasonNetwork(p.getUniqueId())))
							.replaceAll("&", "§"));
					
				}
			}.runTaskLater(BanSystemSpigot.getInstance(), 20*BanSystemSpigot.config.getInt("Ban.KickDelay.inSecconds"));
		}
	}
}
