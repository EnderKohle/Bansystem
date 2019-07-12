package net.coalcube.bansystem.core;

import java.util.List;

import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.User;

public interface BanSystem {

	public static final BanSystem[] BANSYSTEM = new BanSystem[1];
	
	public static BanSystem getInstance() {
		return BANSYSTEM[0];
	}
	
	public static void setInstance(BanSystem bs) {
		BANSYSTEM[0] = bs;
	}
	
	public List<User> getAllPlayers();
	
	public User getConsole();
	
	public String getVersion();
	
	public void onEnable();
	
	public void onDisable();
	
	public User getUser(String name);
	
	public void disconnect(User u, String msg);
	
	public Config getMessages();
	
}
