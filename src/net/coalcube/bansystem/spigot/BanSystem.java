package net.coalcube.bansystem.spigot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.coalcube.bansystem.spigot.command.CMDban;
import net.coalcube.bansystem.spigot.command.CMDbansystem;
import net.coalcube.bansystem.spigot.command.CMDcheck;
import net.coalcube.bansystem.spigot.command.CMDdeletehistory;
import net.coalcube.bansystem.spigot.command.CMDhistory;
import net.coalcube.bansystem.spigot.command.CMDkick;
import net.coalcube.bansystem.spigot.command.CMDunban;
import net.coalcube.bansystem.spigot.command.CMDunmute;
import net.coalcube.bansystem.spigot.listener.ChatListener;
import net.coalcube.bansystem.spigot.listener.LoginListener;
import net.coalcube.bansystem.spigot.util.MySQL;
import net.coalcube.bansystem.spigot.util.Type;
import net.coalcube.bansystem.spigot.util.UUIDFetcher;
import net.coalcube.bansystem.spigot.util.UpdateChecker;

public class BanSystem extends JavaPlugin {

	public static Plugin plugin;
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
	public static YamlConfiguration config;
	public static YamlConfiguration messages;
	
	@Override
	public void onEnable() {
		
		plugin = this;
		
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
					+ "Type VARCHAR(100));");
			
			mysql.update("CREATE TABLE IF NOT EXISTS banhistory ("
							+ " UUID VARCHAR(100),"
							+ " Ende VARCHAR(100),"
							+ " Grund VARCHAR(100),"
							+ " Ersteller VARCHAR(100),"
							+ " Erstelldatum VARCHAR(100),"
							+ " IP VARCHAR(100),"
							+ " Type VARCHAR(100),"
							+ " duration DOUBLE);");
		}
		new BukkitRunnable() {
			
			@Override
			public void run() {
				UUIDFetcher.clearCache();
				
			}
		}.runTaskLaterAsynchronously(this, 1);
		
		Bukkit.getConsoleSender().sendMessage(BanSystem.PREFIX+"§7Das BanSystem wurde gestartet.");
		
		if(config.getString("VPN.serverIP").equals("00.00.00.00") && config.getBoolean("VPN.autoban.enable")) 
			Bukkit.getConsoleSender().sendMessage(BanSystem.PREFIX+"§cBitte trage die IP des Servers in der config.yml ein.");
		
		try {
			if(new UpdateChecker(this, 65863).checkForUpdates()) {
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
		
		Bukkit.getConsoleSender().sendMessage(BanSystem.PREFIX+"§7Das BanSystem wurde gestoppt.");
		
		super.onDisable();
	}
	
	private void init() {
		PluginManager pm = Bukkit.getPluginManager();
		
		this.getCommand("ban").setExecutor(new CMDban());
		this.getCommand("check").setExecutor(new CMDcheck());
		this.getCommand("deletehistory").setExecutor(new CMDdeletehistory());
		this.getCommand("history").setExecutor(new CMDhistory());
		this.getCommand("kick").setExecutor(new CMDkick());
		this.getCommand("unban").setExecutor(new CMDunban());
		this.getCommand("unmute").setExecutor(new CMDunmute());
		this.getCommand("bansystem").setExecutor(new CMDbansystem());
		this.getCommand("bansys").setExecutor(new CMDbansystem());
		
		pm.registerEvents(new LoginListener(), this);
		pm.registerEvents(new ChatListener(), this);
	}
	
	private void createConfig() {
		try {
			File configfile = new File(this.getDataFolder(), "config.yml");
			if(!this.getDataFolder().exists()) {
				this.getDataFolder().mkdir();
			}
			if(!configfile.exists()) {
				configfile.createNewFile();
				
				config = YamlConfiguration.loadConfiguration(configfile);
				
				config.addDefault("mysql.user", "root");
				config.addDefault("mysql.password", "sicherespasswort");
				config.addDefault("mysql.database", "Bansystem");
				config.addDefault("mysql.host", "localhost");
				config.addDefault("mysql.port", 3306);
				
				config.addDefault("IPautoban.enable", false);
				config.addDefault("IPautoban.banid", 9);
				
				config.addDefault("VPN.enable", true);
				config.addDefault("VPN.autoban.enable", true);
				config.addDefault("VPN.autoban.ID", 11); 
				config.addDefault("VPN.serverIP", "00.00.00.00");
				
				config.addDefault("IDs.1.reason", "Unerlaubte Clientmodification/Hackclient");
				config.addDefault("IDs.1.onlyAdmins", false);
				config.addDefault("IDs.1.type", Type.NETWORK.toString());
				config.addDefault("IDs.1.lvl.1.duration", 86400 * 30);
				config.addDefault("IDs.1.lvl.2.duration", -1);

				config.addDefault("IDs.2.reason", "Reportmissbrauch");
				config.addDefault("IDs.2.onlyAdmins", false);
				config.addDefault("IDs.2.type", Type.NETWORK.toString());
				config.addDefault("IDs.2.lvl.1.duration", 86400 * 10);
				config.addDefault("IDs.2.lvl.2.duration", -1);
				
				config.addDefault("IDs.3.reason", "Unangebrachter Skin/Name");
				config.addDefault("IDs.3.onlyAdmins", false);
				config.addDefault("IDs.3.type", Type.NETWORK.toString());
				config.addDefault("IDs.3.lvl.1.duration", 86400 * 30);
				config.addDefault("IDs.3.lvl.2.duration", -1);
				
				config.addDefault("IDs.4.reason", "Crossteaming");
				config.addDefault("IDs.4.onlyAdmins", false);
				config.addDefault("IDs.4.type", Type.NETWORK.toString());
				config.addDefault("IDs.4.lvl.1.duration", 86400 * 10);
				config.addDefault("IDs.4.lvl.2.duration", -1);
				
				config.addDefault("IDs.5.reason", "Bugusing");
				config.addDefault("IDs.5.onlyAdmins", false);
				config.addDefault("IDs.5.type", Type.NETWORK.toString());
				config.addDefault("IDs.5.lvl.1.duration", 86400 * 10);
				config.addDefault("IDs.5.lvl.2.duration", -1);
				
				config.addDefault("IDs.6.reason", "Chat Verhalten");
				config.addDefault("IDs.6.onlyAdmins", false);
				config.addDefault("IDs.6.type", Type.CHAT.toString());
				config.addDefault("IDs.6.lvl.1.duration", 86400 * 1);
				config.addDefault("IDs.6.lvl.2.duration", 86400 * 7);
				config.addDefault("IDs.6.lvl.3.duration", 86400 * 14);
				config.addDefault("IDs.6.lvl.4.duration", 86400 * 30);
				config.addDefault("IDs.6.lvl.5.duration", -1);
				
				config.addDefault("IDs.7.reason", "Werbung");
				config.addDefault("IDs.7.onlyAdmins", false);
				config.addDefault("IDs.7.type", Type.CHAT.toString());
				config.addDefault("IDs.7.lvl.1.duration", 86400 * 7);
				config.addDefault("IDs.7.lvl.2.duration", 86400 * 14);
				config.addDefault("IDs.7.lvl.3.duration", 86400 * 30);
				config.addDefault("IDs.7.lvl.4.duration", -1);
				
				config.addDefault("IDs.8.reason", "Alt-Account");
				config.addDefault("IDs.8.onlyAdmins", false);
				config.addDefault("IDs.8.type", Type.NETWORK.toString());
				config.addDefault("IDs.8.lvl.1.duration", -1);
				
				config.addDefault("IDs.9.reason", "Bannumgehung");
				config.addDefault("IDs.9.onlyAdmins", false);
				config.addDefault("IDs.9.type", Type.NETWORK.toString());
				config.addDefault("IDs.9.lvl.1.duration", -1);

				config.addDefault("IDs.10.reason", "Sicherheitsbann");
				config.addDefault("IDs.10.onlyAdmins", false);
				config.addDefault("IDs.10.type", Type.NETWORK.toString());
				config.addDefault("IDs.10.lvl.1.duration", -1);
				
				config.addDefault("IDs.11.reason", "VPN");
				config.addDefault("IDs.11.onlyAdmins", false);
				config.addDefault("IDs.11.type", Type.NETWORK.toString());
				config.addDefault("IDs.11.lvl.1.duration", -1);
				
				config.addDefault("IDs.99.reason", "§4EXTREM");
				config.addDefault("IDs.99.onlyAdmins", true);
				config.addDefault("IDs.99.type", Type.NETWORK.toString());
				config.addDefault("IDs.99.lvl.1.duration", -1);
				
				config.save(configfile);
			}
			File messagesfile = new File(this.getDataFolder(), "messages.yml");
			if(!messagesfile.exists()) {
				messagesfile.createNewFile();
				
				messages = YamlConfiguration.loadConfiguration(messagesfile);
				
				messages.addDefault("prefix", "&8&l┃ &cBanSystem &8» &7");
				messages.addDefault("NoPermissionMessage", "%P%§cDafür hast du keine Rechte!");
				messages.addDefault("NoPlayerMessage", "%P%§cDu bist kein Spieler!");
				messages.addDefault("Playerdoesnotexist", "%P%§cDieser Spieler existiert nicht!");
				messages.addDefault("PlayerNotFound", "%P%§cDer Spieler wurde nicht gefunden.");
				messages.addDefault("NoMySQLconnection", "%P%§cDie Datenbankverbindung besteht nicht. Wende dich bitte an einen Administrator.");
				messages.addDefault("Playernotbanned", "%P%§cDieser Spieler ist nicht gebannt/gemuted!");
				
				messages.addDefault("Ban.Network.Screen", Arrays.asList(new String[] {"§8§m----------------------§r", 
																			   " ", 
																			   "§4Du wurdest §lGebannt!", 
																			   "§7Grund §8» §c%Reason%", 
																			   "§7Verbleibende Zeit §8» §3%ReamingTime%", 
																			   " ", 
																			   "§8§m----------------------"}));
				messages.addDefault("Ban.Network.autounban", "%P%§e%player% §7wurde §eautomatisch §7entbannt.");
				messages.addDefault("Ban.Chat.Screen", Arrays.asList(new String[] {"§8§m----------------------------", 
																		   	"§4Du bist aus dem chat gebannt!", 
																		   	"§7Grund §8» §c%reason%",
																		   	"§7Verbleibende Zeit §8» §c%reamingtime%",
																		   	"§8§m----------------------------"}));
				messages.addDefault("Ban.Chat.autounmute", "%P%§e%player% §7wurde §eautomatisch §7entmuted.");
				messages.addDefault("Ban.success", "%P%§7Du hast §e%Player% §7erfolgreich §cgebannt/gemuted.");
				messages.addDefault("Ban.notify", Arrays.asList(new String[] {"%P%§8§m-------------------", 
																	   "%P%§e%player% §7wurde Gebannt.",
																	   "%P%Grund §8» §c%reason%",
																	   "%P%Verbleibende Zeit §8» §c%reamingTime%",
																	   "%P%Von §8» §c%banner%",
																	   "%P%Type §8» §c%type%",
																	   "%P%§8§m-------------------"}));
				messages.addDefault("Ban.cannotbanteammembers", "%P%§cDu kannst keine Teammitglieder bannen.");
				messages.addDefault("Ban.onlyadmins", "%P%§cDas dürfen nur Admins und Owner!");
				messages.addDefault("Ban.invalidinput", "%P%§cUngültige Eingabe!");
				messages.addDefault("Ban.alreadybanned", "%P%§cDieser Spieler ist berreits gebannt/gemuted.");
				messages.addDefault("Ban.usage", "%P%§cBenutze §8» §e/ban §8<§7Spieler§8> §8<§7ID§8>");
				messages.addDefault("Ban.ID.Listlayout.heading", "§8§m------------§8» §4Bann IDs §8«§m------------");
				messages.addDefault("Ban.ID.Listlayout.IDs.general", " §e%ID% §8» §c%reason%");
				messages.addDefault("Ban.ID.Listlayout.IDs.onlyadmins", " §e%ID% §8» §c%reason% §8» §4nur für Admins und Owner");
				messages.addDefault("Ban.ID.NoPermission", "%P%§cFür diese ID hast du keine Berechtigung!");
				
				messages.addDefault("Check.usage", "%P%§cBenutze §8» §e/check §8<§7Spieler§8>");
				
				messages.addDefault("Deletehistory.notify", "%P%Die History von §e%player% §7wurde von §e%sender% §7gelöscht.");
				messages.addDefault("Deletehistory.success", "%P%Die History von §e%player% §7wurde gelöscht.");
				messages.addDefault("Deletehistory.usage", "%P%§cBenutze §8» §e/deletehistory §8<§7Spieler§8>");
				
				messages.addDefault("History.historynotfound", "%P%§cDieser Spieler hat keine History");
				messages.addDefault("History.usage", "%P%§cBenutze §8» §e/history §8<§7Spieler§8>");
				
				messages.addDefault("Kick.usage", "%P%§cBenutze §8» §e/kick §8<§7Spieler§8> §8[§7Grund§8]");
				messages.addDefault("Kick.cannotkickyouselfe", "%P%§cDu kannst dich nicht selbst Kicken!");
				messages.addDefault("Kick.cannotkickteammembers", "%P%§cDu kannst keine Teammitglieder Kicken.");
				messages.addDefault("Kick.noreason.screen", "\n §cDu wurdest vom Netzwerk §4§lgekickt§c! \n \n");
				messages.addDefault("Kick.noreason.notify", Arrays.asList(new String[] {"%P%§8§m------------------------------",
																				 "%P%§7Der Spieler §e%player%",
																				 "%P%§7wurde von §e%sender% §cgekickt.",
																				 "%P%§8§m------------------------------"}));
				messages.addDefault("Kick.reason.screen", "\n §cDu wurdest vom Netzwerk §4§lgekickt§c!\n \n§7Grund §8» §c%reason%\n\n");
				messages.addDefault("Kick.reason.notify", Arrays.asList(new String[] {"%P%§8§m------------------------------",
																			   "%P%§7Der Spieler §e%player%",
																			   "%P%§7wurde von §e%sender% §cgekickt.",
																			   "%P%§7Grund §8» §e%reason%",
																			   "%P%§8§m------------------------------"}));
				messages.addDefault("Kick.success", "%P%§7Der Spieler §e%player% §7wurde gekickt.");
				
				messages.addDefault("Unban.success", "%P%§e%player% §7wurde §2erfolgeich §7entbannt.");
				messages.addDefault("Unban.notify", "%P%§e%player% §7wurde von §e%sender% §7entbannt.");
				messages.addDefault("Unban.notbanned", "%P%§e%player% §cist nicht gebannt.");
				messages.addDefault("Unban.usage", "%P%§cBenutze §8» §e/unban §8<§7Spieler§8>");
				
				messages.addDefault("Unmute.usage", "%P%§cBenutze §8» §e/unmute §8<§7Spieler§8>");
				messages.addDefault("Unmute.success", "%P%§7Die Schweigepflicht von §e%player% §7wurde §2aufgehoben!");
				messages.addDefault("Unmute.notmuted", "%P%§e%player% §cist nicht gemuted.");
				messages.addDefault("Unmute.notify", "%P%§e%player% §7wurde von §e%sender% §7entmuted.");
				
				messages.addDefault("VPN.warning", "%P%§e%player% §chat sich mit einer VPN verbunden!");
				
				messages.save(messagesfile);
				
			}
			messages = YamlConfiguration.loadConfiguration(messagesfile);
			config = YamlConfiguration.loadConfiguration(configfile);
		} catch (IOException e) {
			System.err.println("[Bansystem] Dateinen konnte nicht erstellt werden.");
		}
	}
	private void loadConfig() {
		try {
			PREFIX = messages.getString("prefix").replaceAll("&", "§");
			NOPERMISSION = messages.getString("NoPermissionMessage").replaceAll("%P%", PREFIX).replaceAll("&", "§");
			NOPLAYER = messages.getString("NoPlayerMessage").replaceAll("%P%", PREFIX).replaceAll("&", "§");
			NODBCONNECTION = messages.getString("NoMySQLconnection").replaceAll("%P%", PREFIX).replaceAll("&", "§");
			
			for(Object ob : messages.getList("Ban.Network.Screen")) {
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
			System.err.println("[Bansystem] Es ist ein Fehler beim laden der Config/messages datei aufgetreten. "+e.getMessage());
		}
	}
}
