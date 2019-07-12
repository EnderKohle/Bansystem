package net.coalcube.bansystem.bungee.util;

import java.util.ArrayList;
import java.util.List;

import net.coalcube.bansystem.core.util.Config;
import net.md_5.bungee.config.Configuration;

public class BungeeConfig implements Config {

	private Configuration config;
	
	public BungeeConfig(Configuration config) {
		this.config = config;
	}
	
	@Override
	public String getString(String key) {
		return config.getString(key);
	}

	@Override
	public boolean getBoolean(String key) {
		return config.getBoolean(key);
	}

	@Override
	public Config getSection(String key) {
		return new BungeeConfig(config.getSection(key));
	}

	@Override
	public List<String> getKeys() {
		List<String> res = new ArrayList<String>();
		res.addAll(config.getKeys());
		return res;
	}

	@Override
	public long getLong(String key) {
		return config.getLong(key);
	}

	@Override
	public List<String> getStringList(String key) {
		return config.getStringList(key);
	}

}
