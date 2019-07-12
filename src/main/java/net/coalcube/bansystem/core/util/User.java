package net.coalcube.bansystem.core.util;

import java.util.UUID;

public interface User {

	public void sendMessage(String msg);
	
	public boolean hasPermission(String perm);
	
	public String getName();
	
	public Object getRawUser();
	
	public UUID getUniqueId();
	
}
