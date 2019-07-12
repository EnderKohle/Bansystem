package net.coalcube.bansystem.core.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Config {

	public String getString(String key);
	
	public boolean getBoolean(String key);
	
	public Config getSection(String key);
	
	public List<String> getKeys();
	
	public long getLong(String key);
	
	public List<String> getStringList(String key);
	
	public void set(String key, Object o);
	
	public void save(File f) throws IOException;
	
	public int getInt(String key);
	
}
