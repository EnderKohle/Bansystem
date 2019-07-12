package net.coalcube.bansystem.spigot.util;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.coalcube.bansystem.core.util.User;

public class SpigotUser implements User {

	private CommandSender sender;

	public SpigotUser(CommandSender sender) {
		this.sender = sender;
	}

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
		return sender instanceof Player ? ((Player) sender).getUniqueId() : null;
	}
	
	@Override
	public boolean equals(Object o) {
		return sender.equals(o);
	}

}
