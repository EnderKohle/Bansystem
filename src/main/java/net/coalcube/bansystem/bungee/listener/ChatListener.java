package net.coalcube.bansystem.bungee.listener;

import net.coalcube.bansystem.bungee.BanSystemBungee;
import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Type;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {
	
	private static Banmanager bm = BanSystemBungee.getBanmanager();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(ChatEvent e) {
		if(BanSystemBungee.mysql.isConnected()) {
			ProxiedPlayer p = (ProxiedPlayer) e.getSender();
			String msg = e.getMessage();
			boolean startsWithBlockedCommnad = false;
			
			for(Object s : BanSystemBungee.config.getList("mute.blockedCommands")) {
				if(msg.startsWith(s.toString())) {
					startsWithBlockedCommnad = true;
				}
			}
			if(startsWithBlockedCommnad || !msg.startsWith("/")) {
				if(bm.isbanned(p.getUniqueId()) && bm.getType(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) == Type.CHAT) {
					if(bm.getEnd(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) > System.currentTimeMillis() || bm.getEnd(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) == -1) {
						e.setCancelled(true);
						for (String message : BanSystemBungee.messages.getStringList("Ban.Chat.Screen")) {
							p.sendMessage(message.replaceAll("%P%", BanSystemBungee.PREFIX).replaceAll("%reason%", bm.getReasonChat(p.getUniqueId()))
									.replaceAll("%reamingtime%", bm.getRemainingTime(p.getUniqueId(), bm.getReasonChat(p.getUniqueId()))).replaceAll("&", "§"));
						}
					} else {
						bm.unmute(p.getUniqueId());
						ProxyServer.getInstance().getConsole().sendMessage(BanSystemBungee.messages.getString("Ban.Chat.autounmute").replaceAll("%P%", BanSystemBungee.PREFIX).replaceAll("%player%", p.getDisplayName()).replaceAll("&", "§"));
						for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
							if(all.hasPermission("system.ban")) {
								all.sendMessage(BanSystemBungee.messages.getString("Ban.Chat.autounmute")
										.replaceAll("%P%", BanSystemBungee.PREFIX)
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
