package net.coalcube.bansystem.spigot.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import net.coalcube.bansystem.core.util.Config;

public class SpigotConfig implements Config {

	private FileConfiguration config;
	
	public SpigotConfig(FileConfiguration config) {
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
		return new SpigotConfigurationSection(config.getConfigurationSection(key));
	}

	@Override
	public List<String> getKeys() {
		List<String> res = new ArrayList<String>();
		res.addAll(config.getKeys(false));
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
