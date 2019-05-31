package net.coalcube.bansystem.bungee.command;

import net.coalcube.bansystem.bungee.BanSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CMDbansystem extends Command {

	public CMDbansystem(String name) {
		super(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender.hasPermission("bansys.bansys")) {
			if(args.length == 0) {
				sender.sendMessage(BanSystem.PREFIX+"§7Benutze §e/bansystem help");
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("help")) {
					
					sender.sendMessage("§8§m--------§8[ §cBanSystem §8]§m--------");
					sender.sendMessage("§e/bansystem help §8» §7Zeigt dir alle Befehle des BanSystems");
					sender.sendMessage("§e/bansystem reload §8» §7Lädt das Plugin neu");
					sender.sendMessage("§e/bansystem version §8» §7Zeigt dir die Version des Plugins");
					sender.sendMessage("§e/ban §8<§7Spieler§8> §8<§7ID§8> §8» §7Bannt/Muted Spieler");
					sender.sendMessage("§e/kick §8<§7Spieler§8> §8[§7Grund§8] §8» §7Kickt einen Spieler");
					sender.sendMessage("§e/unban §8<§7Spieler§8> §8» §7Entbannt einen Spieler");
					sender.sendMessage("§e/unmute §8<§7Spieler§8> §8» §7Entmuted einen Spieler");
					sender.sendMessage("§e/check §8<§7Spieler§8> §8» §7Prüft ob ein Spieler bestraft ist");
					sender.sendMessage("§e/history §8<§7Spieler§8> §8» §7Zeigt die History von einem Spieler");
					sender.sendMessage("§e/deletehistory §8<§7Spieler§8> §8» §7Löscht die History von einem Spieler");
					sender.sendMessage("§8§m-----------------------------");
					
				} else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					
					sender.sendMessage(BanSystem.PREFIX+"§7Plugin wird §eneu geladen§7.");
					
					BanSystem.plugin.onDisable();
					BanSystem.plugin.onEnable();
					
					sender.sendMessage(BanSystem.PREFIX+"§7Plugin §ereloaded§7.");
					
				} else if(args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
					sender.sendMessage(BanSystem.PREFIX+"§7Version §8» §e"+BanSystem.plugin.getDescription().getVersion());
				}
			} else {
				sender.sendMessage(BanSystem.PREFIX+"§7Benutze §e/bansystem help");
			}
		} else {
			sender.sendMessage(BanSystem.PREFIX+"§7BanSystem by §eTobi");
		}
	}

}
