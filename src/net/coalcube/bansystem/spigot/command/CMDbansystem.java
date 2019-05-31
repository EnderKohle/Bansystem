package net.coalcube.bansystem.spigot.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.coalcube.bansystem.spigot.BanSystem;

public class CMDbansystem implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
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
					
					return true;
					
				} else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					
					sender.sendMessage(BanSystem.PREFIX+"§7Plugin wird §eneu geladen§7.");
					
					BanSystem.plugin.onDisable();
					BanSystem.plugin.onEnable();
					
					sender.sendMessage(BanSystem.PREFIX+"§7Plugin §ereloaded§7.");
					
					return true;
					
				} else if(args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
					sender.sendMessage(BanSystem.PREFIX+"§7Version §8» §e"+BanSystem.plugin.getDescription().getVersion());
					return true;
				}
			} else {
				sender.sendMessage(BanSystem.PREFIX+"§7Benutze §e/bansystem help");
				return false;
			}
		} else {
			sender.sendMessage(BanSystem.PREFIX+"§7BanSystem by §eTobi");
			return false;
		}
		return false;
	}
}
