package net.coalcube.bansystem.bungee;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.coalcube.bansystem.bungee.command.CMDban;
import net.coalcube.bansystem.bungee.command.CMDbansystem;
import net.coalcube.bansystem.bungee.command.CMDcheck;
import net.coalcube.bansystem.bungee.command.CMDdeletehistory;
import net.coalcube.bansystem.bungee.command.CMDhistory;
import net.coalcube.bansystem.bungee.command.CMDkick;
import net.coalcube.bansystem.bungee.command.CMDunban;
import net.coalcube.bansystem.bungee.command.CMDunmute;
import net.coalcube.bansystem.bungee.listener.ChatListener;
import net.coalcube.bansystem.bungee.listener.LoginListener;
import net.coalcube.bansystem.bungee.util.MySQL;
import net.coalcube.bansystem.bungee.util.UpdateChecker;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BanSystem extends Plugin {

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
	public static Configuration config;
	public static Configuration messages;
	
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
		ProxyServer.getInstance().getScheduler().schedule(this, () -> {
			UUIDFetcher.clearCache();
		}, 1, 1, TimeUnit.HOURS);
		
		ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(BanSystem.PREFIX+"§7Das BanSystem wurde gestartet."));
		
		if(config.getString("VPN.serverIP").equals("00.00.00.00") && config.getBoolean("VPN.autoban.enable")) 
			ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(BanSystem.PREFIX+"§cBitte trage die IP des Servers in der config.yml ein."));
		
		try {
			if(new UpdateChecker(this, 65863).checkForUpdates()) {
				ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(PREFIX+"§cEin neues Update ist verfügbar."));
				ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(PREFIX+"§7Lade es dir unter §ehttps://www.spigotmc.org/resources/bansystem-mit-ids.65863/ §7runter um aktuell zu bleiben."));
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
		
		PluginManager pm = ProxyServer.getInstance().getPluginManager();
		
		pm.unregisterCommand(new CMDban("ban"));
		pm.unregisterCommand(new CMDcheck("check"));
		pm.unregisterCommand(new CMDdeletehistory("deletehistory"));
		pm.unregisterCommand(new CMDhistory("history"));
		pm.unregisterCommand(new CMDkick("kick"));
		pm.unregisterCommand(new CMDunban("unban"));
		pm.unregisterCommand(new CMDunmute("unmute"));
		pm.unregisterCommand(new CMDbansystem("bansystem"));
		pm.unregisterCommand(new CMDbansystem("bansys"));
		
		pm.unregisterListener(new LoginListener());
		pm.unregisterListener(new ChatListener());
		
		ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(BanSystem.PREFIX+"§7Das BanSystem wurde gestoppt."));
		
		super.onDisable();
	}
	
	private void init() {
		PluginManager pm = ProxyServer.getInstance().getPluginManager();
		
		pm.registerCommand(this, new CMDban("ban"));
		pm.registerCommand(this, new CMDcheck("check"));
		pm.registerCommand(this, new CMDdeletehistory("deletehistory"));
		pm.registerCommand(this, new CMDhistory("history"));
		pm.registerCommand(this, new CMDkick("kick"));
		pm.registerCommand(this, new CMDunban("unban"));
		pm.registerCommand(this, new CMDunmute("unmute"));
		pm.registerCommand(this, new CMDbansystem("bansystem"));
		pm.registerCommand(this, new CMDbansystem("bansys"));
		
		pm.registerListener(this, new LoginListener());
		pm.registerListener(this, new ChatListener());
	}
	
	private void createConfig() {
		try {
			File configfile = new File(this.getDataFolder(), "config.yml");
			if(!this.getDataFolder().exists()) {
				this.getDataFolder().mkdir();
			}
			if(!configfile.exists()) {
				configfile.createNewFile();
				
				config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configfile);
				
				config.set("mysql.user", "root");
				config.set("mysql.password", "sicherespasswort");
				config.set("mysql.database", "Bansystem");
				config.set("mysql.host", "localhost");
				config.set("mysql.port", 3306);
				
				config.set("IPautoban.enable", false);
				config.set("IPautoban.banid", 9);
				
				config.set("VPN.enable", true);
				config.set("VPN.autoban.enable", true);
				config.set("VPN.autoban.ID", 11); 
				config.set("VPN.serverIP", "00.00.00.00");
				
				config.set("mute.blockedCommands", Arrays.asList(new String[] {"/msg","/tell"}));
				
				config.set("IDs.1.reason", "Unerlaubte Clientmodification/Hackclient");
				config.set("IDs.1.onlyAdmins", false);
				config.set("IDs.1.type", Type.NETWORK.toString());
				config.set("IDs.1.lvl.1.duration", 86400 * 30);
				config.set("IDs.1.lvl.2.duration", -1);

				config.set("IDs.2.reason", "Reportmissbrauch");
				config.set("IDs.2.onlyAdmins", false);
				config.set("IDs.2.type", Type.NETWORK.toString());
				config.set("IDs.2.lvl.1.duration", 86400 * 10);
				config.set("IDs.2.lvl.2.duration", -1);
				
				config.set("IDs.3.reason", "Unangebrachter Skin/Name");
				config.set("IDs.3.onlyAdmins", false);
				config.set("IDs.3.type", Type.NETWORK.toString());
				config.set("IDs.3.lvl.1.duration", 86400 * 30);
				config.set("IDs.3.lvl.2.duration", -1);
				
				config.set("IDs.4.reason", "Crossteaming");
				config.set("IDs.4.onlyAdmins", false);
				config.set("IDs.4.type", Type.NETWORK.toString());
				config.set("IDs.4.lvl.1.duration", 86400 * 10);
				config.set("IDs.4.lvl.2.duration", -1);
				
				config.set("IDs.5.reason", "Bugusing");
				config.set("IDs.5.onlyAdmins", false);
				config.set("IDs.5.type", Type.NETWORK.toString());
				config.set("IDs.5.lvl.1.duration", 86400 * 10);
				config.set("IDs.5.lvl.2.duration", -1);
				
				config.set("IDs.6.reason", "Chat Verhalten");
				config.set("IDs.6.onlyAdmins", false);
				config.set("IDs.6.type", Type.CHAT.toString());
				config.set("IDs.6.lvl.1.duration", 86400 * 1);
				config.set("IDs.6.lvl.2.duration", 86400 * 7);
				config.set("IDs.6.lvl.3.duration", 86400 * 14);
				config.set("IDs.6.lvl.4.duration", 86400 * 30);
				config.set("IDs.6.lvl.5.duration", -1);
				
				config.set("IDs.7.reason", "Werbung");
				config.set("IDs.7.onlyAdmins", false);
				config.set("IDs.7.type", Type.CHAT.toString());
				config.set("IDs.7.lvl.1.duration", 86400 * 7);
				config.set("IDs.7.lvl.2.duration", 86400 * 14);
				config.set("IDs.7.lvl.3.duration", 86400 * 30);
				config.set("IDs.7.lvl.4.duration", -1);
				
				config.set("IDs.8.reason", "Alt-Account");
				config.set("IDs.8.onlyAdmins", false);
				config.set("IDs.8.type", Type.NETWORK.toString());
				config.set("IDs.8.lvl.1.duration", -1);
				
				config.set("IDs.9.reason", "Bannumgehung");
				config.set("IDs.9.onlyAdmins", false);
				config.set("IDs.9.type", Type.NETWORK.toString());
				config.set("IDs.9.lvl.1.duration", -1);

				config.set("IDs.10.reason", "Sicherheitsbann");
				config.set("IDs.10.onlyAdmins", false);
				config.set("IDs.10.type", Type.NETWORK.toString());
				config.set("IDs.10.lvl.1.duration", -1);
				
				config.set("IDs.11.reason", "VPN");
				config.set("IDs.11.onlyAdmins", false);
				config.set("IDs.11.type", Type.NETWORK.toString());
				config.set("IDs.11.lvl.1.duration", -1);
				
				config.set("IDs.99.reason", "§4EXTREM");
				config.set("IDs.99.onlyAdmins", true);
				config.set("IDs.99.type", Type.NETWORK.toString());
				config.set("IDs.99.lvl.1.duration", -1);
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configfile);
			}
			File messagesfile = new File(this.getDataFolder(), "messages.yml");
			if(!messagesfile.exists()) {
				messagesfile.createNewFile();
				
				messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesfile);
				
				messages.set("prefix", "&8&l┃ &cBanSystem &8» &7");
				messages.set("NoPermissionMessage", "%P%§cDafür hast du keine Rechte!");
				messages.set("NoPlayerMessage", "%P%§cDu bist kein Spieler!");
				messages.set("Playerdoesnotexist", "%P%§cDieser Spieler existiert nicht!");
				messages.set("PlayerNotFound", "%P%§cDer Spieler wurde nicht gefunden.");
				messages.set("NoMySQLconnection", "%P%§cDie Datenbankverbindung besteht nicht. Wende dich bitte an einen Administrator.");
				messages.set("Playernotbanned", "%P%§cDieser Spieler ist nicht gebannt/gemuted!");
				
				messages.set("Ban.Network.Screen", Arrays.asList(new String[] {"§8§m----------------------§r", 
																			   " ", 
																			   "§4Du wurdest §lGebannt!", 
																			   "§7Grund §8» §c%Reason%", 
																			   "§7Verbleibende Zeit §8» §3%ReamingTime%", 
																			   " ", 
																			   "§8§m----------------------"}));
				messages.set("Ban.Network.autounban", "%P%§e%player% §7wurde §eautomatisch §7entbannt.");
				messages.set("Ban.Chat.Screen", Arrays.asList(new String[] {"§8§m----------------------------", 
																		   	"§4Du bist aus dem chat gebannt!", 
																		   	"§7Grund §8» §c%reason%",
																		   	"§7Verbleibende Zeit §8» §c%reamingtime%",
																		   	"§8§m----------------------------"}));
				messages.set("Ban.Chat.autounmute", "%P%§e%player% §7wurde §eautomatisch §7entmuted.");
				messages.set("Ban.success", "%P%§7Du hast §e%Player% §7erfolgreich §cgebannt/gemuted.");
				messages.set("Ban.notify", Arrays.asList(new String[] {"%P%§8§m-------------------", 
																	   "%P%§e%player% §7wurde Gebannt.",
																	   "%P%Grund §8» §c%reason%",
																	   "%P%Verbleibende Zeit §8» §c%reamingTime%",
																	   "%P%Von §8» §c%banner%",
																	   "%P%Type §8» §c%type%",
																	   "%P%§8§m-------------------"}));
				messages.set("Ban.cannotbanteammembers", "%P%§cDu kannst keine Teammitglieder bannen.");
				messages.set("Ban.onlyadmins", "%P%§cDas dürfen nur Admins und Owner!");
				messages.set("Ban.invalidinput", "%P%§cUngültige Eingabe!");
				messages.set("Ban.alreadybanned", "%P%§cDieser Spieler ist berreits gebannt/gemuted.");
				messages.set("Ban.usage", "%P%§cBenutze §8» §e/ban §8<§7Spieler§8> §8<§7ID§8>");
				messages.set("Ban.ID.Listlayout.heading", "§8§m------------§8» §4Bann IDs §8«§m------------");
				messages.set("Ban.ID.Listlayout.IDs.general", " §e%ID% §8» §c%reason%");
				messages.set("Ban.ID.Listlayout.IDs.onlyadmins", " §e%ID% §8» §c%reason% §8» §4nur für Admins und Owner");
				messages.set("Ban.ID.NoPermission", "%P%§cFür diese ID hast du keine Berechtigung!");
				
				messages.set("Check.usage", "%P%§cBenutze §8» §e/check §8<§7Spieler§8>");
				
				messages.set("Deletehistory.notify", "%P%Die History von §e%player% §7wurde von §e%sender% §7gelöscht.");
				messages.set("Deletehistory.success", "%P%Die History von §e%player% §7wurde gelöscht.");
				messages.set("Deletehistory.usage", "%P%§cBenutze §8» §e/deletehistory §8<§7Spieler§8>");
				
				messages.set("History.historynotfound", "%P%§cDieser Spieler hat keine History");
				messages.set("History.usage", "%P%§cBenutze §8» §e/history §8<§7Spieler§8>");
				
				messages.set("Kick.usage", "%P%§cBenutze §8» §e/kick §8<§7Spieler§8> §8[§7Grund§8]");
				messages.set("Kick.cannotkickyouselfe", "%P%§cDu kannst dich nicht selbst Kicken!");
				messages.set("Kick.cannotkickteammembers", "%P%§cDu kannst keine Teammitglieder Kicken.");
				messages.set("Kick.noreason.screen", "\n §cDu wurdest vom Netzwerk §4§lgekickt§c! \n \n");
				messages.set("Kick.noreason.notify", Arrays.asList(new String[] {"%P%§8§m------------------------------",
																				 "%P%§7Der Spieler §e%player%",
																				 "%P%§7wurde von §e%sender% §cgekickt.",
																				 "%P%§8§m------------------------------"}));
				messages.set("Kick.reason.screen", "\n §cDu wurdest vom Netzwerk §4§lgekickt§c!\n \n§7Grund §8» §c%reason%\n\n");
				messages.set("Kick.reason.notify", Arrays.asList(new String[] {"%P%§8§m------------------------------",
																			   "%P%§7Der Spieler §e%player%",
																			   "%P%§7wurde von §e%sender% §cgekickt.",
																			   "%P%§7Grund §8» §e%reason%",
																			   "%P%§8§m------------------------------"}));
				messages.set("Kick.success", "%P%§7Der Spieler §e%player% §7wurde gekickt.");
				
				messages.set("Unban.success", "%P%§e%player% §7wurde §2erfolgeich §7entbannt.");
				messages.set("Unban.notify", "%P%§e%player% §7wurde von §e%sender% §7entbannt.");
				messages.set("Unban.notbanned", "%P%§e%player% §cist nicht gebannt.");
				messages.set("Unban.usage", "%P%§cBenutze §8» §e/unban §8<§7Spieler§8>");
				
				messages.set("Unmute.usage", "%P%§cBenutze §8» §e/unmute §8<§7Spieler§8>");
				messages.set("Unmute.success", "%P%§7Die Schweigepflicht von §e%player% §7wurde §2aufgehoben!");
				messages.set("Unmute.notmuted", "%P%§e%player% §cist nicht gemuted.");
				messages.set("Unmute.notify", "%P%§e%player% §7wurde von §e%sender% §7entmuted.");
				
				messages.set("VPN.warning", "%P%§e%player% §chat sich mit einer VPN verbunden!");
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(messages, messagesfile);
				
			}
			messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messagesfile);
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configfile);
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
