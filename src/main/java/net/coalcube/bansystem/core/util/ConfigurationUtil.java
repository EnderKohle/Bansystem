package net.coalcube.bansystem.core.util;

import java.util.Arrays;

public class ConfigurationUtil {

	public static void initConfig(Config config) {
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

		config.set("mute.blockedCommands", Arrays.asList(new String[] { "/msg", "/tell" }));

		config.set("needReason.Unban", false);
		config.set("needReason.Unmute", false);

		config.set("blacklist.words.enable", true);
		config.set("blacklist.words.autoban.enable", true);
		config.set("blacklist.words.autoban.id", 6);

		config.set("blacklist.ads.enable", true);
		config.set("blacklist.ads.autoban.enable", true);
		config.set("blacklist.ads.autoban.id", 7);

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
	}

	public static void initMessages(Config messages) {

		messages.set("prefix", "§8§l┃ &cBanSystem §8» §7");
		messages.set("NoPermissionMessage", "%P%§cDafür hast du keine Rechte!");
		messages.set("NoPlayerMessage", "%P%§cDu bist kein Spieler!");
		messages.set("Playerdoesnotexist", "%P%§cDieser Spieler existiert nicht!");
		messages.set("PlayerNotFound", "%P%§cDer Spieler wurde nicht gefunden.");
		messages.set("NoMySQLconnection",
				"%P%§cDie Datenbankverbindung besteht nicht. Wende dich bitte an einen Administrator.");
		messages.set("Playernotbanned", "%P%§cDieser Spieler ist nicht gebannt/gemuted!");

		messages.set("Ban.Network.Screen",
				Arrays.asList(new String[] { "§8§m----------------------§r", " ", "§4Du wurdest §lGebannt!",
						"§7Grund §8» §c%Reason%", "§7Verbleibende Zeit §8» §3%ReamingTime%", " ",
						"§8§m----------------------" }));
		messages.set("Ban.Network.autounban", "%P%§e%player% §7wurde §eautomatisch §7entbannt.");
		messages.set("Ban.Chat.Screen",
				Arrays.asList(new String[] { "§8§m----------------------------",
						"§4Du bist aus dem chat gebannt!", "§7Grund §8» §c%reason%",
						"§7Verbleibende Zeit §8» §c%reamingtime%", "§8§m----------------------------" }));
		messages.set("Ban.Chat.autounmute", "%P%§e%player% §7wurde §eautomatisch §7entmuted.");
		messages.set("Ban.success", "%P%§7Du hast §e%Player% §7erfolgreich §cgebannt/gemuted.");
		messages.set("Ban.notify",
				Arrays.asList(new String[] { "%P%§8§m-------------------", "%P%§e%player% §7wurde Gebannt.",
						"%P%Grund §8» §c%reason%", "%P%Verbleibende Zeit §8» §c%reamingTime%",
						"%P%Von §8» §c%banner%", "%P%Type §8» §c%type%", "%P%§8§m-------------------" }));
		messages.set("Ban.cannotbanteammembers", "%P%§cDu kannst keine Teammitglieder bannen.");
		messages.set("Ban.onlyadmins", "%P%§cDas dürfen nur Admins und Owner!");
		messages.set("Ban.invalidinput", "%P%§cUngültige Eingabe!");
		messages.set("Ban.alreadybanned", "%P%§cDieser Spieler ist berreits gebannt/gemuted.");
		messages.set("Ban.usage", "%P%§cBenutze §8» §e/ban §8<§7Spieler§8> §8<§7ID§8>");
		messages.set("Ban.ID.Listlayout.heading", "§8§m------------§8» §4Bann IDs §8«§m------------");
		messages.set("Ban.ID.Listlayout.IDs.general", " §e%ID% §8» §c%reason%");
		messages.set("Ban.ID.Listlayout.IDs.onlyadmins",
				" §e%ID% §8» §c%reason% §8» §4nur für Admins und Owner");
		messages.set("Ban.ID.NoPermission", "%P%§cFür diese ID hast du keine Berechtigung!");

		messages.set("Check.usage", "%P%§cBenutze §8» §e/check §8<§7Spieler§8>");

		messages.set("Deletehistory.notify",
				"%P%Die History von §e%player% §7wurde von §e%sender% §7gelöscht.");
		messages.set("Deletehistory.success", "%P%Die History von §e%player% §7wurde gelöscht.");
		messages.set("Deletehistory.usage", "%P%§cBenutze §8» §e/deletehistory §8<§7Spieler§8>");

		messages.set("History.historynotfound", "%P%§cDieser Spieler hat keine History");
		messages.set("History.usage", "%P%§cBenutze §8» §e/history §8<§7Spieler§8>");

		messages.set("Kick.usage", "%P%§cBenutze §8» §e/kick §8<§7Spieler§8> §8[§7Grund§8]");
		messages.set("Kick.cannotkickyouselfe", "%P%§cDu kannst dich nicht selbst Kicken!");
		messages.set("Kick.cannotkickteammembers", "%P%§cDu kannst keine Teammitglieder Kicken.");
		messages.set("Kick.noreason.screen", "\n §cDu wurdest vom Netzwerk §4§lgekickt§c! \n \n");
		messages.set("Kick.noreason.notify",
				Arrays.asList(new String[] { "%P%§8§m------------------------------",
						"%P%§7Der Spieler §e%player%", "%P%§7wurde von §e%sender% §cgekickt.",
						"%P%§8§m------------------------------" }));
		messages.set("Kick.reason.screen",
				"\n §cDu wurdest vom Netzwerk §4§lgekickt§c!\n \n§7Grund §8» §c%reason%\n\n");
		messages.set("Kick.reason.notify",
				Arrays.asList(new String[] { "%P%§8§m------------------------------",
						"%P%§7Der Spieler §e%player%", "%P%§7wurde von §e%sender% §cgekickt.",
						"%P%§7Grund §8» §e%reason%", "%P%§8§m------------------------------" }));
		messages.set("Kick.success", "%P%§7Der Spieler §e%player% §7wurde gekickt.");

		messages.set("Unban.success", "%P%§e%player% §7wurde §2erfolgeich §7entbannt.");
		messages.set("Unban.notify", "%P%§e%player% §7wurde von §e%sender% §7entbannt.");
		messages.set("Unban.notbanned", "%P%§e%player% §cist nicht gebannt.");
		messages.set("Unban.usage", "%P%§cBenutze §8» §e/unban §8<§7Spieler§8>");

		messages.set("Unban.needreason.usage", "%P%§cBenutze §8» §e/unban §8<§7Spieler§8> §8<§7Grund§8>");
		messages.set("Unban.needreason.success", "%P%§e%player% §7wurde §2erfolgeich §7entbannt.");
		messages.set("Unban.needreason.notify",
				Arrays.asList(new String[] { "%P%§8§m------------------------------",
						"%P%§e%player% §7wurde von §e%sender% §7entbannt.", "%P%§7Grund §8» §e%reason%",
						"%P%§8§m------------------------------" }));

		messages.set("Unmute.usage", "%P%§cBenutze §8» §e/unmute §8<§7Spieler§8>");
		messages.set("Unmute.success", "%P%§7Die Schweigepflicht von §e%player% §7wurde §2aufgehoben!");
		messages.set("Unmute.notmuted", "%P%§e%player% §cist nicht gemuted.");
		messages.set("Unmute.notify", "%P%§e%player% §7wurde von §e%sender% §7entmuted.");

		messages.set("Unmute.needreason.usage", "%P%§cBenutze §8» §e/unmute §8<§7Spieler§8> §8<§7Grund§8>");
		messages.set("Unmute.needreason.success",
				"%P%§7Die Schweigepflicht von §e%player% §7wurde §2aufgehoben!");
		messages.set("unmute.needreason.notify",
				Arrays.asList(new String[] { "%P%§8§m------------------------------",
						"%P%§e%player% §7wurde von §e%sender% §7entmuted.", "%P%§7Grund §8» §e%reason%",
						"%P%§8§m------------------------------" }));

		messages.set("VPN.warning", "%P%§e%player% §chat sich mit einer VPN verbunden!");

		messages.set("bansystem.usage", "%P%§7Benutze §e/bansystem help");
		messages.set("bansystem.help",
				Arrays.asList(new String[] { "§8§m--------§8[ §cBanSystem §8]§m--------",
						"§e/bansystem help §8» §7Zeigt dir alle Befehle des BanSystems",
						"§e/bansystem reload §8» §7Lädt das Plugin neu",
						"§e/bansystem version §8» §7Zeigt dir die Version des Plugins",
						"§e/ban §8<§7Spieler§8> §8<§7ID§8> §8» §7Bannt/Muted Spieler",
						"§e/kick §8<§7Spieler§8> §8[§7Grund§8] §8» §7Kickt einen Spieler",
						"§e/unban §8<§7Spieler§8> §8» §7Entbannt einen Spieler",
						"§e/unmute §8<§7Spieler§8> §8» §7Entmuted einen Spieler",
						"§e/check §8<§7Spieler§8> §8» §7Prüft ob ein Spieler bestraft ist",
						"§e/history §8<§7Spieler§8> §8» §7Zeigt die History von einem Spieler",
						"§e/deletehistory §8<§7Spieler§8> §8» §7Löscht die History von einem Spieler",
						"§8§m-----------------------------" }));
		messages.set("bansystem.reload.process", "%P%§7Plugin wird §eneu geladen§7.");
		messages.set("bansystem.reload.finished", "%P%§7Plugin §eneu geladen§7.");
		messages.set("bansystem.version", "%P%§7Version §8» §e%ver%");

		messages.set("autoban.words.notify",
				Arrays.asList(new String[] { "%P%§8§m------------------------------",
						"%P%§e%player% wurde §7für sein Chatverhalten", "%P%§7automatisch §egebannt/gemuted§7.",
						"%P%§7Grund §8» §e%reason%", "%P%§7Nachricht §8» §e%message%",
						"%P%§8§m------------------------------" }));

		messages.set("autoban.ads.notify",
				Arrays.asList(new String[] { "%P%§8§m------------------------------",
						"%P%§e%player% wurde §7für sein Chatverhalten", "%P%§7automatisch §egebannt/gemuted§7.",
						"%P%§7Grund §8» §e%reason%", "%P%§7Nachricht §8» §e%message%",
						"%P%§8§m------------------------------" }));

	}

	public static void initBlacklist(Config blacklist) {
		/**
		 * TODO add more blacklisted words
		 */

		blacklist.set("Words", Arrays.asList(new String[] { "Arsch", "Nutte", "Hure" }));

		/**
		 * TODO add more blacklisted ads
		 */

		blacklist.set("Ads",
				Arrays.asList(new String[] { ".de", ".net", ".at", ".com", ".be", ".eu", ".shop" }));
	}

}
