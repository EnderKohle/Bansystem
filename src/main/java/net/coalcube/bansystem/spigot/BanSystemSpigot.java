package net.coalcube.bansystem.spigot;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.coalcube.bansystem.core.BanSystem;
import net.coalcube.bansystem.core.command.CMDban;
import net.coalcube.bansystem.core.command.CMDbansystem;
import net.coalcube.bansystem.core.command.CMDcheck;
import net.coalcube.bansystem.core.command.CMDdeletehistory;
import net.coalcube.bansystem.core.command.CMDhistory;
import net.coalcube.bansystem.core.command.CMDkick;
import net.coalcube.bansystem.core.command.CMDunban;
import net.coalcube.bansystem.core.command.CMDunmute;
import net.coalcube.bansystem.core.util.Banmanager;
import net.coalcube.bansystem.core.util.Config;
import net.coalcube.bansystem.core.util.ConfigurationUtil;
import net.coalcube.bansystem.core.util.MySQL;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.coalcube.bansystem.core.util.UpdateChecker;
import net.coalcube.bansystem.core.util.User;
import net.coalcube.bansystem.spigot.listener.AsyncPlayerChatListener;
import net.coalcube.bansystem.spigot.listener.PlayerCommandPreprocessListener;
import net.coalcube.bansystem.spigot.listener.PlayerJoinListener;
import net.coalcube.bansystem.spigot.util.SpigotConfig;
import net.coalcube.bansystem.spigot.util.SpigotUser;

@SuppressWarnings("deprecation")
public class BanSystemSpigot extends JavaPlugin implements BanSystem {

	private static Plugin instance;
	private static Banmanager banmanager;
	
	public static String PREFIX,
						 NOPERMISSION,
						 NOPLAYER,
						 NODBCONNECTION;
	public static String Banscreen;
	private static String hostname,
						 database,
						 user,
						 pw;
	private static int port;
	public static MySQL mysql;
	public static Config config;
	public static Config messages;
	
	@Override
	public void onEnable() {
		
		BanSystem.setInstance(this);
		
		instance = this;
		
		banmanager = new Banmanager();
		
		init();
		
		createConfig();
		loadConfig();
		
		mysql = new MySQL(hostname, port, database, user, pw);
		if(mysql.isConnected()) {
			mysql.update("CREATE TABLE IF NOT EXISTS ban ("
					+ "UUID VARCHAR(100), "
					+ "Ende VARCHAR(100), "
					+ "Grund VARCHAR(100), "
					+ "Ersteller VARCHAR(100), "
					+ "IP VARCHAR(100), "
					+ "Type VARCHAR(100),"
					+ "ID VARCHAR(10));");
			
			mysql.update("CREATE TABLE IF NOT EXISTS banhistory ("
							+ " UUID VARCHAR(100),"
							+ " Ende VARCHAR(100),"
							+ " Grund VARCHAR(100),"
							+ " Ersteller VARCHAR(100),"
							+ " Erstelldatum VARCHAR(100),"
							+ " IP VARCHAR(100),"
							+ " Type VARCHAR(100),"
							+ " duration DOUBLE,"
							+ " ID VARCHAR(10));");
			
			ResultSet rs = mysql.getResult("SHOW TABLES LIKE 'ban'");
			try {
				while(rs.next()) {
					ResultSet rs1 = mysql.getResult("SHOW COLUMNS FROM `ban` LIKE 'ID'");
					boolean existsColumn = false;
					while(rs1.next()) {
						existsColumn = true;
					}
					if(!existsColumn)
						mysql.update("ALTER TABLE `ban` ADD `ID` VARCHAR(10) NOT NULL AFTER `Type`;");
					mysql.update("ALTER TABLE `ban` ADD `ID` VARCHAR(10) NOT NULL AFTER `Type`;");
					ResultSet rs2 = mysql.getResult("SELECT * FROM `ban`");
					while (rs2.next()) {
						mysql.update("UPDATE `ban` SET ID = '" + RandomStringUtils.randomAlphanumeric(8) + "' WHERE UUID = '" + rs.getString("UUID") + "' and Type = '" + rs.getString("Type") + "';");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			ResultSet rs1 = mysql.getResult("SHOW TABLES LIKE 'banhistory'");
			try {
				while(rs1.next()) {
					ResultSet rs2 = mysql.getResult("SHOW COLUMNS FROM `ban` LIKE 'ID'");
					boolean existsColumn = false;
					while(rs2.next()) {
						existsColumn = true;
					}
					if(!existsColumn)
						mysql.update("ALTER TABLE `ban` ADD `ID` VARCHAR(10) NOT NULL AFTER `Type`;");
					mysql.update("ALTER TABLE `banhistory` ADD `ID` VARCHAR(10) NOT NULL AFTER `duration`;");
					ResultSet rs3 = mysql.getResult("SELECT * FROM `banhistory`");
					while (rs3.next()) {
						mysql.update("UPDATE `banhistory` SET ID = '" + new Banmanager().getID(UUID.fromString(rs.getString("UUID")), Type.valueOf(rs.getString("Type")))+ "' WHERE UUID = '" + rs.getString("UUID") + "' and Type = '" + rs.getString("Type") + "';");
					}
					ResultSet rs4 = mysql.getResult("SELECT * FROM `banhistory` WHERE ID = ''");
					while (rs4.next()) {
						mysql.update("UPDATE `ban` SET ID = '" + UUID.randomUUID().toString().substring(0, 8) + "';");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(config.getBoolean("needReason.Unban")) {
				mysql.update("CREATE TABLE IF NOT EXISTS unban ("
							+ " ID VARCHAR(10),"
							+ " unbanner VARCHAR(100),"
							+ " Grund VARCHAR(300));");
			}
			if(config.getBoolean("needReason.Unmute")) {
				mysql.update("CREATE TABLE IF NOT EXISTS unmute ("
							+ " ID VARCHAR(10),"
							+ " unbanner VARCHAR(100)"  
							+ " Grund VARCHAR(300));");
			}
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				UUIDFetcher.clearCache();
				
			}
		}.runTaskLaterAsynchronously(this, 1);
		
		Bukkit.getConsoleSender().sendMessage(BanSystemSpigot.PREFIX+"§7Das BanSystem wurde gestartet.");
		
		if(config.getString("VPN.serverIP").equals("00.00.00.00") && config.getBoolean("VPN.autoban.enable")) 
			Bukkit.getConsoleSender().sendMessage(BanSystemSpigot.PREFIX+"§cBitte trage die IP des Servers in der config.yml ein.");
		
		try {
			if(new UpdateChecker(65863).checkForUpdates()) {
				Bukkit.getConsoleSender().sendMessage(PREFIX+"§cEin neues Update ist verfügbar.");
				Bukkit.getConsoleSender().sendMessage(PREFIX+"§7Lade es dir unter §ehttps://www.spigotmc.org/resources/bansystem-mit-ids.65863/ §7runter um aktuell zu bleiben.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		super.onEnable();
	}
	@Override
	public void onDisable() {
		if(mysql.isConnected())
			mysql.disconnect();
		
		AsyncPlayerChatEvent.getHandlerList().unregister(getInstance());
		PlayerCommandPreprocessEvent.getHandlerList().unregister(getInstance());
		PlayerQuitEvent.getHandlerList().unregister(getInstance());
		PlayerJoinEvent.getHandlerList().unregister(getInstance());
		PlayerPreLoginEvent.getHandlerList().unregister(getInstance());
		
		Banscreen = "";
		
		Bukkit.getConsoleSender().sendMessage(BanSystemSpigot.PREFIX+"§7Das BanSystem wurde gestoppt.");
		
		super.onDisable();
	}
	
	private void init() {
		PluginManager pm = Bukkit.getPluginManager();
		
		this.getCommand("ban").setExecutor(new CommandWrapper(new CMDban(banmanager, config, messages, mysql), true));
		this.getCommand("check").setExecutor(new CommandWrapper(new CMDcheck(banmanager, mysql, messages), true));
		this.getCommand("deletehistory").setExecutor(new CommandWrapper(new CMDdeletehistory(banmanager, messages, mysql), true));
		this.getCommand("history").setExecutor(new CommandWrapper(new CMDhistory(banmanager, messages, mysql), true));
		this.getCommand("kick").setExecutor(new CommandWrapper(new CMDkick(messages, mysql), true));
		this.getCommand("unban").setExecutor(new CommandWrapper(new CMDunban(banmanager, mysql, messages, config), true));
		this.getCommand("unmute").setExecutor(new CommandWrapper(new CMDunmute(banmanager, messages, config, mysql), true));
		this.getCommand("bansystem").setExecutor(new CommandWrapper(new CMDbansystem(messages), false));
		this.getCommand("bansys").setExecutor(new CommandWrapper(new CMDbansystem(messages), false));
		
		pm.registerEvents(new PlayerJoinListener(), this);
		pm.registerEvents(new PlayerCommandPreprocessListener(), this);
		pm.registerEvents(new AsyncPlayerChatListener(), this);
	}
	
	private void createConfig() {
		try {
			File configfile = new File(this.getDataFolder(), "config.yml");
			if(!this.getDataFolder().exists()) {
				this.getDataFolder().mkdir();
			}
			if(!configfile.exists()) {
				configfile.createNewFile();
				
				config = new SpigotConfig(YamlConfiguration.loadConfiguration(configfile));
				
				ConfigurationUtil.initConfig(config);
				
				config.save(configfile);
			}
			File messagesfile = new File(this.getDataFolder(), "messages.yml");
			if(!messagesfile.exists()) {
				messagesfile.createNewFile();
				
				messages = new SpigotConfig(YamlConfiguration.loadConfiguration(messagesfile));
				
				ConfigurationUtil.initMessages(messages);
				
				messages.save(messagesfile);
				
			}
			messages = new SpigotConfig(YamlConfiguration.loadConfiguration(messagesfile));
			config = new SpigotConfig(YamlConfiguration.loadConfiguration(configfile));
		} catch (IOException e) {
			System.err.println("[Bansystem] Dateien konnten nicht erstellt werden.");
		}
	}
	private void loadConfig() {
		try {
			PREFIX = messages.getString("prefix").replaceAll("&", "§");
			NOPERMISSION = messages.getString("NoPermissionMessage").replaceAll("%P%", PREFIX).replaceAll("&", "§");
			NOPLAYER = messages.getString("NoPlayerMessage").replaceAll("%P%", PREFIX).replaceAll("&", "§");
			NODBCONNECTION = messages.getString("NoMySQLconnection").replaceAll("%P%", PREFIX).replaceAll("&", "§");
			
			for(Object ob : messages.getStringList("Ban.Network.Screen")) {
				if(Banscreen == null) {
					Banscreen = ob.toString().replaceAll("%P%", PREFIX)+"\n";
				} else Banscreen = Banscreen+ob.toString().replaceAll("%P%", PREFIX)+"\n";
			}
			user = config.getString("mysql.user");
			hostname = config.getString("mysql.host");
			port = config.getInt("mysql.port");
			pw = config.getString("mysql.password");
			database = config.getString("mysql.database");
		} catch (NullPointerException e) {
			System.err.println("[Bansystem] Es ist ein Fehler beim laden der Config/messages Datei aufgetreten. ["+e.getMessage()+"]");
		}
	}
	public static Plugin getInstance() {
		return instance;
	}
	public static Banmanager getBanmanager() {
		return banmanager;
	}
	@Override
	public List<User> getAllPlayers() {
		List<User> users = new ArrayList<User>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			users.add(new SpigotUser(p));
		}
		return users;
	}
	@Override
	public User getConsole() {
		return new SpigotUser(Bukkit.getServer().getConsoleSender());
	}
	@Override
	public String getVersion() {
		return this.getDescription().getVersion();
	}
	@Override
	public User getUser(String name) {
		return new SpigotUser(Bukkit.getServer().getPlayer(name));
	}
	
	@Override
	public void disconnect(User u, String msg) {
		if(u.getRawUser() instanceof Player) {
			((Player) u.getRawUser()).kickPlayer(msg);
		}
	}
	
	@Override
	public Config getMessages() {
		return messages;
	}
	
	@Override
	public Config getConfiguration() {
		return config;
	}
	
	@Override
	public MySQL getMySQL() {
		return mysql;
	}
}
