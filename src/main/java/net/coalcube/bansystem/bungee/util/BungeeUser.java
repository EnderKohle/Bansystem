package net.coalcube.bansystem.bungee.util;

import java.util.UUID;

import net.coalcube.bansystem.core.util.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeUser implements User {

	private CommandSender sender;

	public BungeeUser(CommandSender sender) {
		this.sender = sender;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendMessage(String msg) {
		sender.sendMessage(msg);
	}

	@Override
	public boolean hasPermission(String perm) {
		return sender.hasPermission(perm);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public Object getRawUser() {
		return sender;
	}

	@Override
	public UUID getUniqueId() {
		return sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : null;
	}
	
	@Override
	public boolean equals(Object o) {
		return sender.equals(o);
	}

}
