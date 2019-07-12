package net.coalcube.bansystem.core.util;

import java.util.List;

public interface Config {

	public String getString(String key);
	
	public boolean getBoolean(String key);
	
	public Config getSection(String key);
	
	public List<String> getKeys();
	
	public long getLong(String key);
	
	public List<String> getStringList(String key);
	
}
