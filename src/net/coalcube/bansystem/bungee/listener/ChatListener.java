package net.coalcube.bansystem.bungee.listener;

import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.bungee.util.Banmanager;
import net.coalcube.bansystem.bungee.util.Type;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {
	private Banmanager bm = new Banmanager();
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(ChatEvent e) {
		if(BanSystem.mysql.isConnected()) {
			ProxiedPlayer p = (ProxiedPlayer) e.getSender();
			String msg = e.getMessage();
			if(msg.startsWith("/msg") || !msg.startsWith("/")) {
				if(bm.isbanned(p.getUniqueId()) && bm.getType(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) == Type.CHAT) {
					if(bm.getEnd(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) > System.currentTimeMillis() || bm.getEnd(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) == -1) {
						e.setCancelled(true);
						for (String message : BanSystem.messages.getStringList("Ban.Chat.Screen")) {
							p.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%reason%", bm.getReasonChat(p.getUniqueId()))
									.replaceAll("%reamingtime%", bm.getRemainingTime(p.getUniqueId(), bm.getReasonChat(p.getUniqueId()))).replaceAll("&", "§"));
						}
					} else {
						bm.unmute(p.getUniqueId());
						ProxyServer.getInstance().getConsole().sendMessage(BanSystem.messages.getString("Ban.Chat.autounmute").replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", p.getDisplayName()).replaceAll("&", "§"));
						for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
							if(all.hasPermission("system.ban")) {
								all.sendMessage(BanSystem.messages.getString("Ban.Chat.autounmute")
										.replaceAll("%P%", BanSystem.PREFIX)
										.replaceAll("%player%", p.getDisplayName())
										.replaceAll("&", "§"));
							}
						}
					}
				}
			}
		}
	}
}
