package net.coalcube.bansystem.spigot.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.spigot.BanSystem;
import net.coalcube.bansystem.spigot.util.Banmanager;

public class PlayerCommandPreprocessListener implements Listener {
	
	private static Banmanager bm = BanSystem.getBanmanager();
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(PlayerCommandPreprocessEvent e) {
		if(BanSystem.mysql.isConnected()) {
			Player p = e.getPlayer();
			String msg = e.getMessage();
			boolean startsWithBlockedCommnad = false;
			
			for(Object s : BanSystem.config.getList("mute.blockedCommands")) {
				if(msg.startsWith(s.toString())) {
					startsWithBlockedCommnad = true;
				}
			}
			
			if(startsWithBlockedCommnad || !msg.startsWith("/")) {
				if(bm.isbanned(p.getUniqueId()) && bm.getType(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) == Type.CHAT) {
					if(bm.getEnd(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) > System.currentTimeMillis() || bm.getEnd(p.getUniqueId(), bm.getReasonChat(p.getUniqueId())) == -1) {
						e.setCancelled(true);
						for (String message : BanSystem.messages.getStringList("Ban.Chat.Screen")) {
							p.sendMessage(message.replaceAll("%P%", BanSystem.PREFIX).replaceAll("%reason%", bm.getReasonChat(p.getUniqueId()))
									.replaceAll("%reamingtime%", bm.getRemainingTime(p.getUniqueId(), bm.getReasonChat(p.getUniqueId()))).replaceAll("&", "§"));
						}
					} else {
						bm.unmute(p.getUniqueId());
						Bukkit.getConsoleSender().sendMessage(BanSystem.messages.getString("Ban.Chat.autounmute").replaceAll("%P%", BanSystem.PREFIX).replaceAll("%player%", p.getDisplayName()).replaceAll("&", "§"));
						for(Player all : Bukkit.getOnlinePlayers()) {
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
