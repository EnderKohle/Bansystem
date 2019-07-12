package net.coalcube.bansystem.bungee.listener;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.coalcube.bansystem.bungee.BanSystemBungee;
import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.URLUtils;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.UpdateChecker;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {
	
	private static Banmanager bm = BanSystemBungee.getBanmanager();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLogin(LoginEvent e) {
		if (BanSystemBungee.mysql.isConnected()) {
			e.registerIntent(BanSystemBungee.getInstance());
			new Thread(new Runnable() {

				@Override
				public void run() {
					PendingConnection con = e.getConnection();

					if (bm.isBannedNetwork(con.getUniqueId())) {
						if (bm.getEnd(con.getUniqueId(), bm.getReasonNetwork(con.getUniqueId())) > System
								.currentTimeMillis()
								|| bm.getEnd(con.getUniqueId(), bm.getReasonNetwork(con.getUniqueId())) == -1) {

							BaseComponent component = new TextComponent(
									BanSystemBungee.Banscreen.replaceAll("%Reason%", bm.getReasonNetwork(con.getUniqueId()))
											.replaceAll("%ReamingTime%", bm.getRemainingTime(con.getUniqueId(),
													bm.getReasonNetwork(con.getUniqueId()))).replaceAll("&", "§"));
							e.setCancelReason(component);
							e.setCancelled(true);
							// p.disconnect(component);
							if (bm.needIP(e.getConnection().getUniqueId())) {
								bm.setIP(e.getConnection().getUniqueId(), con.getAddress().getAddress());
							}
						} else {
							bm.unban(con.getUniqueId());
							ProxyServer.getInstance().getConsole()
									.sendMessage(BanSystemBungee.messages.getString("Ban.Network.autounban")
											.replaceAll("%P%", BanSystemBungee.PREFIX).replaceAll("%player%", con.getName()).replaceAll("&", "§"));
							for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
								if (all.hasPermission("bansys.notify")) {
									all.sendMessage(BanSystemBungee.messages.getString("Ban.Network.autounban")
											.replaceAll("%P%", BanSystemBungee.PREFIX).replaceAll("%player%", con.getName()).replaceAll("&", "§"));
								}
							}
						}
					}
					if (!e.isCancelled()) {
						ProxyServer.getInstance().getScheduler().schedule(BanSystemBungee.getInstance(), new Runnable() {

							@Override
							public void run() {
								ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getConnection().getName());
								if (p instanceof ProxiedPlayer) {

									if (BanSystemBungee.config.getBoolean("VPN.enable")) {
										if (URLUtils
												.isVPN(p.getAddress().getAddress().toString().replaceAll("/", ""))) {
											if (BanSystemBungee.config.getBoolean("VPN.autoban.enable")) {
												bm.ban(e.getConnection().getUniqueId(),
														BanSystemBungee.config.getInt("VPN.autoban.ID"), "CONSOLE",
														p.getAddress().getAddress());
											} else {
												for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
													all.sendMessage(BanSystemBungee.messages.getString("VPN.warning")
															.replaceAll("%P%", BanSystemBungee.PREFIX)
															.replaceAll("%player%", p.getDisplayName())
															.replaceAll("&", "§"));
												}
											}
										}
									}

									if (p.hasPermission("bansys.ban.admin")) {
										try {
											if (new UpdateChecker(65863).checkForUpdates()) {
												TextComponent comp = new TextComponent(BanSystemBungee.PREFIX
														+ "§7Lade es dir unter §ehttps://www.spigotmc.org/resources/bansystem-mit-ids.65863/ §7runter um aktuell zu bleiben.");

												p.sendMessage(new TextComponent(
														BanSystemBungee.PREFIX + "§cEin neues Update ist verfügbar."));

												comp.setClickEvent(new ClickEvent(Action.OPEN_URL,
														"https://www.spigotmc.org/resources/bansystem-mit-ids.65863/"));
												comp.setHoverEvent(new HoverEvent(
														net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
														new ComponentBuilder("Klicke um zur Webseite zu gelangen")
																.create()));

												p.sendMessage(comp);
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
											if (BanSystemBungee.config.getBoolean("IPautoban.enable")) {
												new Banmanager().ban(p.getUniqueId(),
														BanSystemBungee.config.getInt("IPautoban.banid"), "CONSOLE",
														p.getAddress().getAddress());
												ProxyServer.getInstance().getConsole()
														.sendMessage(BanSystemBungee.PREFIX + "§cDer 2. Account von §e"
																+ names + " §cwurde automatisch gebannt für §e"
																+ BanSystemBungee.config.getString("IDs."
																		+ BanSystemBungee.config.getInt("IPautoban.banid")
																		+ ".reason")
																+ "§c.");
												for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
													if (all.hasPermission("bansys.notify")) {
														all.sendMessage(BanSystemBungee.PREFIX + "§cDer 2. Account von §e"
																+ names + " §cwurde automatisch gebannt für §e"
																+ BanSystemBungee.config.getString("IDs."
																		+ BanSystemBungee.config.getInt("IPautoban.banid")
																		+ ".reason")
																+ "§c.");
													}
												}
												BaseComponent component = new TextComponent(BanSystemBungee.Banscreen
														.replaceAll("%Reason%", bm.getReasonNetwork(con.getUniqueId()))
														.replaceAll("%ReamingTime%",
																bm.getRemainingTime(con.getUniqueId(),
																		bm.getReasonNetwork(con.getUniqueId()))));
												e.setCancelReason(component);
												e.setCancelled(true);
												p.disconnect(component);
											} else {
												ProxyServer.getInstance().getConsole()
														.sendMessage(BanSystemBungee.PREFIX + "§e" + p.getName()
																+ " §cist womöglich ein 2. Account von §e" + names);
												for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
													if (all.hasPermission("bansys.notify")) {
														all.sendMessage(BanSystemBungee.PREFIX + "§e" + p.getName()
																+ " §cist womöglich ein 2. Account von §e" + names);
													}
												}
											}
										}
									}
								}
							}
						}, 1, TimeUnit.SECONDS);
					}
					e.completeIntent(BanSystemBungee.getInstance());
				}
			}).start();
		}
	}
}
