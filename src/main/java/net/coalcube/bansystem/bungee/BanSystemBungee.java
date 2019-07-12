package net.coalcube.bansystem.bungee;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.coalcube.bansystem.bungee.listener.ChatListener;
import net.coalcube.bansystem.bungee.listener.LoginListener;
import net.coalcube.bansystem.bungee.util.BungeeConfig;
import net.coalcube.bansystem.bungee.util.BungeeUser;
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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BanSystemBungee extends Plugin implements BanSystem {

	private static Plugin instance;
	private static Banmanager banmanager;

	public static String PREFIX, NOPERMISSION, NOPLAYER, NODBCONNECTION;
	public static String Banscreen;
	private static String hostname, database, user, pw;
	private static int port;
	public static MySQL mysql;
	public static Config config;
	public static Config messages;
	public static Config blacklist;

	@Override
	public void onEnable() {

		BanSystem.setInstance(this);

		instance = this;

		banmanager = new Banmanager();

		init();

		createConfig();
		loadConfig();

		mysql = new MySQL(hostname, port, database, user, pw);
		if (mysql.isConnected()) {
			mysql.update("CREATE TABLE IF NOT EXISTS ban (" + "UUID VARCHAR(100), " + "Ende VARCHAR(100), "
					+ "Grund VARCHAR(100), " + "Ersteller VARCHAR(100), " + "IP VARCHAR(100), "
					+ "Type VARCHAR(100));");

			mysql.update("CREATE TABLE IF NOT EXISTS banhistory (" + " UUID VARCHAR(100)," + " Ende VARCHAR(100),"
					+ " Grund VARCHAR(100)," + " Ersteller VARCHAR(100)," + " Erstelldatum VARCHAR(100),"
					+ " IP VARCHAR(100)," + " Type VARCHAR(100)," + " duration DOUBLE);");

			ResultSet rs = mysql.getResult("SHOW TABLES LIKE 'ban'");
			try {
				while (rs.next()) {
					ResultSet rs1 = mysql.getResult("SHOW COLUMNS FROM `ban` LIKE 'ID'");
					boolean existsColumn = false;
					while (rs1.next()) {
						existsColumn = true;
					}
					if (!existsColumn)
						mysql.update("ALTER TABLE `ban` ADD `ID` VARCHAR(10) NOT NULL AFTER `Type`;");
					ResultSet rs2 = mysql.getResult("SELECT * FROM `ban`");
					while (rs2.next()) {
						mysql.update("UPDATE `ban` SET ID = '" + UUID.randomUUID().toString().substring(0, 8)
								+ "' WHERE UUID = '" + rs.getString("UUID") + "' and Type = '" + rs.getString("Type")
								+ "';");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			ResultSet rs1 = mysql.getResult("SHOW TABLES LIKE 'banhistory'");
			try {
				while (rs1.next()) {
					ResultSet rs2 = mysql.getResult("SHOW COLUMNS FROM `ban` LIKE 'ID'");
					boolean existsColumn = false;
					while (rs2.next()) {
						existsColumn = true;
					}
					if (!existsColumn)
						mysql.update("ALTER TABLE `ban` ADD `ID` VARCHAR(10) NOT NULL AFTER `Type`;");
					mysql.update("ALTER TABLE `banhistory` ADD `ID` VARCHAR(10) NOT NULL AFTER `duration`;");
					ResultSet rs3 = mysql.getResult("SELECT * FROM `banhistory`");
					while (rs3.next()) {
						mysql.update("UPDATE `banhistory` SET ID = '"
								+ new Banmanager().getID(UUID.fromString(rs.getString("UUID")),
										Type.valueOf(rs.getString("Type")))
								+ "' WHERE UUID = '" + rs.getString("UUID") + "' and Type = '" + rs.getString("Type")
								+ "';");
					}
					ResultSet rs4 = mysql.getResult("SELECT * FROM `banhistory` WHERE ID = ''");
					while (rs4.next()) {
						mysql.update("UPDATE `ban` SET ID = '" + UUID.randomUUID().toString().substring(0, 8) + "';");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (config.getBoolean("needReason.Unban")) {
				mysql.update("CREATE TABLE IF NOT EXISTS unban (" + " ID VARCHAR(10)," + " unbanner VARCHAR(100),"
						+ " Grund VARCHAR(100));");
			}
			if (config.getBoolean("needReason.Unmute")) {
				mysql.update("CREATE TABLE IF NOT EXISTS unmute (" + " ID VARCHAR(10)," + " unbanner VARCHAR(100)"
						+ " Grund VARCHAR(100));");
			}
		}
		ProxyServer.getInstance().getScheduler().schedule(this, () -> {
			UUIDFetcher.clearCache();
		}, 1, 1, TimeUnit.HOURS);

		ProxyServer.getInstance().getConsole()
				.sendMessage(new TextComponent(BanSystemBungee.PREFIX + "§7Das BanSystem wurde gestartet."));

		if (config.getString("VPN.serverIP").equals("00.00.00.00") && config.getBoolean("VPN.autoban.enable"))
			ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(
					BanSystemBungee.PREFIX + "§cBitte trage die IP des Servers in der config.yml ein."));

		try {
			if (new UpdateChecker(65863).checkForUpdates()) {
				ProxyServer.getInstance().getConsole()
						.sendMessage(new TextComponent(PREFIX + "§cEin neues Update ist verfügbar."));
				ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(PREFIX
						+ "§7Lade es dir unter §ehttps://www.spigotmc.org/resources/bansystem-mit-ids.65863/ §7runter um aktuell zu bleiben."));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onEnable();
	}

	@Override
	public void onDisable() {
		if (mysql.isConnected())
			mysql.disconnect();

		PluginManager pm = ProxyServer.getInstance().getPluginManager();

		pm.unregisterListeners(this);

		ProxyServer.getInstance().getConsole()
				.sendMessage(new TextComponent(BanSystemBungee.PREFIX + "§7Das BanSystem wurde gestoppt."));

		super.onDisable();
	}

	private void init() {
		PluginManager pm = ProxyServer.getInstance().getPluginManager();

		pm.registerCommand(this, new CommandWrapper("ban", new CMDban(banmanager, messages, config, mysql), true));
		pm.registerCommand(this, new CommandWrapper("check", new CMDcheck(banmanager, mysql, messages), true));
		pm.registerCommand(this, new CommandWrapper("deletehistory", new CMDdeletehistory(banmanager, messages, mysql), true));
		pm.registerCommand(this, new CommandWrapper("history", new CMDhistory(banmanager, messages, mysql), true));
		pm.registerCommand(this, new CommandWrapper("kick", new CMDkick(messages, mysql), true));
		pm.registerCommand(this, new CommandWrapper("unban", new CMDunban(banmanager, mysql, messages, config), true));
		pm.registerCommand(this, new CommandWrapper("unmute", new CMDunmute(banmanager, messages, config, mysql), true));
		pm.registerCommand(this, new CommandWrapper("bansystem", new CMDbansystem(messages), false));
		pm.registerCommand(this, new CommandWrapper("bansys", new CMDbansystem(messages), false));

		pm.registerListener(this, new LoginListener());
		pm.registerListener(this, new ChatListener());
	}

	private void createConfig() {
		try {
			File configfile = new File(this.getDataFolder(), "config.yml");
			if (!this.getDataFolder().exists()) {
				this.getDataFolder().mkdir();
			}
			if (!configfile.exists()) {
				configfile.createNewFile();

				config = new BungeeConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(configfile));

				ConfigurationUtil.initConfig(config);
				
				config.save(configfile);
			}
			File messagesfile = new File(this.getDataFolder(), "messages.yml");
			if (!messagesfile.exists()) {
				messagesfile.createNewFile();

				messages = new BungeeConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesfile));

				ConfigurationUtil.initMessages(messages);
				
				messages.save(messagesfile);

			}
			File blacklistfile = new File(this.getDataFolder(), "blacklist.yml");
			if (!blacklistfile.exists()) {
				blacklist = new BungeeConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(blacklistfile));

				ConfigurationUtil.initBlacklist(blacklist);
				
				blacklist.save(blacklistfile);
			}
			messages = new BungeeConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesfile));
			config = new BungeeConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(configfile));
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

			for (Object ob : messages.getStringList("Ban.Network.Screen")) {
				if (Banscreen == null) {
					Banscreen = ob.toString().replaceAll("%P%", PREFIX) + "\n";
				} else
					Banscreen = Banscreen + ob.toString().replaceAll("%P%", PREFIX) + "\n";
			}
			user = config.getString("mysql.user");
			hostname = config.getString("mysql.host");
			port = config.getInt("mysql.port");
			pw = config.getString("mysql.password");
			database = config.getString("mysql.database");
		} catch (NullPointerException e) {
			System.err.println("[Bansystem] Es ist ein Fehler beim laden der Config/messages Datei aufgetreten. "
					+ e.getMessage());
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
		for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			users.add(new BungeeUser(p));
		}
		return users;
	}

	@Override
	public User getConsole() {
		return new BungeeUser(ProxyServer.getInstance().getConsole());
	}

	@Override
	public String getVersion() {
		return this.getDescription().getVersion();
	}

	@Override
	public User getUser(String name) {
		return new BungeeUser(ProxyServer.getInstance().getPlayer(name));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void disconnect(User u, String msg) {
		if (u.getRawUser() instanceof ProxiedPlayer) {
			((ProxiedPlayer) u.getRawUser()).disconnect(msg);
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
