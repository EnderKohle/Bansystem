package net.coalcube.bansystem.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import net.coalcube.bansystem.spigot.BanSystemSpigot;

public class Banmanager {
	
	public void ban(String grund, long seconds, String ersteller, Type type, UUID uuid, InetAddress ip) {
		String id = UUID.randomUUID().toString().substring(0, 8);
		long end;
		long current = System.currentTimeMillis();
		if (seconds == -1) {
			end = -1;
		} else {
			long millis = seconds * 1000;
			end = current + millis;
		}
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String zeitstempel = simpleDateFormat.format(date);
		try {
			if (ip != null)
				ip = InetAddress.getByName(ip.toString().replaceAll("/", ""));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		BanSystemSpigot.mysql.update("INSERT INTO ban (UUID, Ende, Grund, Ersteller, IP, Type, ID) VALUES ('" + uuid + "','" + end
				+ "','" + grund + "','" + ersteller + "','" + ip + "','" + type + "', '" + id + "');");
		if (ip != null) {
			
			BanSystemSpigot.mysql
					.update("INSERT INTO `banhistory` (UUID, Ende, Grund, Ersteller, Erstelldatum, IP, Type, duration, ID) "
							+ "VALUES ('" + uuid + "'," + "'" + end + "'," + "'" + grund + "'," + "'" + ersteller + "',"
							+ "'"
							+ zeitstempel
							+ "'," + "'" + ip.getHostAddress() + "'," + "'" + type + "', '" + seconds + "', '" + id + "');");
		} else
			BanSystemSpigot.mysql
					.update("INSERT INTO `banhistory` (UUID, Ende, Grund, Ersteller, Erstelldatum, Type, duration, ID) "
							+ "VALUES ('" + uuid + "'," + "'" + end + "'," + "'" + grund + "'," + "'" + ersteller + "',"
							+ "'"
							+ zeitstempel
							+ "'," + "'" + type + "', '" + seconds + "', '" + id + "');");
	}
	
	public void ban(UUID uuid, int id, String ersteller, InetAddress ip) {
		String reason = "";
		byte lvl;
		Type type = null;
		long dauer = 0;
		for (String key : BanSystemSpigot.config.getConfigurationSection("IDs").getKeys(false)) {
			if (id == Integer.parseInt(key)) {
				reason = BanSystemSpigot.config.getString("IDs." + key + ".reason");
				type = Type.valueOf(BanSystemSpigot.config.getString("IDs." + key + ".type"));
				if (hashistory(uuid, reason)) {
					lvl = (byte) (getLevel(uuid, reason) + 1);
				} else {
					lvl = 1;
				}
				for (String lvlkey : BanSystemSpigot.config.getConfigurationSection("IDs." + key + ".lvl").getKeys(false)) {
					if ((byte) Byte.valueOf(lvlkey) == lvl) {
						dauer = BanSystemSpigot.config.getLong("IDs." + key + ".lvl." + lvlkey + ".duration");
					}
				}
				lvl = (byte) (lvl + 1);
				if (dauer == 0) {
					dauer = -1;
				}
			}
		}
		ban(reason, dauer, ersteller, type, uuid, ip);
	}
	
	public void unban(UUID id) {
		BanSystemSpigot.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.NETWORK + "'");
	}
	
	public void unban(UUID id, String banid, UUID unbanner, String reason) {
		BanSystemSpigot.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.NETWORK + "'");
		BanSystemSpigot.mysql.update("INSERT INTO `unban` (ID, unbanner, Grund) VALUES ('" + banid + "', '" + unbanner + "', '" + reason + "');");
	}
	
	public void unban(UUID id, String banid, String unbanner, String reason) {
		BanSystemSpigot.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.NETWORK + "'");
		BanSystemSpigot.mysql.update("INSERT INTO `unban` (ID, unbanner, Grund) VALUES ('" + banid + "', '" + unbanner + "', '" + reason + "');");
	}
	
	public void unmute(UUID id) {
		BanSystemSpigot.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.CHAT + "'");
	}
	
	public void unmute(UUID id, String banid, UUID unbanner, String reason) {
		BanSystemSpigot.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.CHAT + "'");
		BanSystemSpigot.mysql.update("INSERT INTO `unmute` (ID, unbanner, Grund) VALUES ('" + banid + "', '" + unbanner + "', '" + reason + "');");
	}
	
	public void unmute(UUID id, String banid, String unbanner, String reason) {
		BanSystemSpigot.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.CHAT + "'");
		BanSystemSpigot.mysql.update("INSERT INTO `unmute` (ID, unbanner, Grund) VALUES ('" + banid + "', '" + unbanner + "', '" + reason + "');");
	}
	
	public boolean isbanned(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT UUID FROM `ban` WHERE UUID='" + id + "'");
		try {
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getReasonNetwork(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql
				.getResult("SELECT Grund FROM `ban` WHERE UUID='" + id + "' AND Type='" + Type.NETWORK + "'");
		try {
			while (rs.next()) {
				return rs.getString("Grund");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Kein Grund angegeben";
	}
	
	public String getReasonChat(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql
				.getResult("SELECT Grund FROM `ban` WHERE UUID='" + id + "' AND Type='" + Type.CHAT + "'");
		try {
			while (rs.next()) {
				return rs.getString("Grund");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Kein Grund angegeben";
	}
	
	public Long getEnd(UUID id, String Grund) {
		try {
			ResultSet rs = BanSystemSpigot.mysql
					.getResult("SELECT Ende FROM `ban` WHERE UUID='" + id + "' AND Grund='" + Grund + "'");
			while (rs.next()) {
				return rs.getLong("Ende");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getBanner(UUID id, Type type) {
		try {
			ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT Ersteller FROM `ban` WHERE UUID='" + id + "' and Type='"+type+"'");
			while (rs.next()) {
				return rs.getString("Ersteller");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "unbekannt";
	}
	
	public void sendHistorys(UUID id, User sender) {
		ResultSet rs = BanSystemSpigot.mysql.getResult(
				"SELECT * FROM `banhistory` WHERE UUID='" + id + "'");
		sender.sendMessage(BanSystemSpigot.PREFIX + "§8§m-------§8» §e" + UUIDFetcher.getName(id) + " §8«§m-------");

		if (hashistory(id)) {
			try {
				while (rs.next()) {
					sender.sendMessage(BanSystemSpigot.PREFIX);
					sender.sendMessage(BanSystemSpigot.PREFIX + "§7Grund §8» §c" + rs.getString("Grund"));
					sender.sendMessage(BanSystemSpigot.PREFIX + "§7Erstelldatum §8» §c" + rs.getString("Erstelldatum"));
					sender.sendMessage(BanSystemSpigot.PREFIX + "§7Enddatum §8» §c"+new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(rs.getLong("Ende"))));
					sender.sendMessage(BanSystemSpigot.PREFIX + "§7Ersteller §8» §c" + rs.getString("Ersteller"));
					sender.sendMessage(BanSystemSpigot.PREFIX + "§7IP §8» §c" + rs.getString("IP"));
					sender.sendMessage(BanSystemSpigot.PREFIX + "§7Type §8» §c" + rs.getString("Type"));
					sender.sendMessage(BanSystemSpigot.PREFIX + "§7ID §8» §c#" + rs.getString("ID"));
					
					long millis = rs.getLong("duration") * 1000;

					int seconds = 0;
					int minutes = 0;
					int hours = 0;
					int days = 0;
					while (millis > 999) {
						millis -= 1000;
						seconds++;
					}
					while (seconds > 59) {
						seconds -= 60;
						minutes++;
					}
					while (minutes > 59) {
						minutes -= 60;
						hours++;
					}
					while (hours > 23) {
						hours -= 24;
						days++;
					}
					String ramingTime = "";
					if (millis != -1) {
						if (days > 0) {
							ramingTime = "§e" + days + " §cTag(e)" + (hours > 0 ? "§e" + hours + ", §cStunde(n)" : "")
									+ (minutes > 0 ? ", §e" + minutes + " §cMinute(n)" : "")
									+ (seconds > 0 ? "und §e" + seconds + " §cSekunde(n)" : "");
						} else if (hours > 0) {
							ramingTime = "§e" + hours + " §cStunde(n), §e" + minutes + " §cMinute(n) und §e" + seconds
									+ " §cSekunde(n)";
						} else if (minutes > 0) {
							ramingTime = "§e" + minutes + " §cMinute(n) und §e" + seconds + " §cSekunde(n)";
						} else {
							ramingTime = "§e" + seconds + " §cSekunde(n)";
						}
					} else {
						ramingTime = "§4§lPERMANENT";
					}

					sender.sendMessage(BanSystemSpigot.PREFIX + "§7Dauer §8» §c" + ramingTime);
					sender.sendMessage(BanSystemSpigot.PREFIX);
					sender.sendMessage(BanSystemSpigot.PREFIX + "§8§m------------------------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			sender.sendMessage(BanSystemSpigot.messages.getString("History.historynotfound"));
		}
	}
	
	public String getRemainingTime(UUID id, String reason) {
		long current = System.currentTimeMillis();
		long end = getEnd(id, reason);
		long millis = end - current;

		int seconds = 0;
		int minutes = 0;
		int hours = 0;
		int days = 0;
		while (millis > 999) {
			millis -= 1000;
			seconds++;
		}
		while (seconds > 59) {
			seconds -= 60;
			minutes++;
		}
		while (minutes > 59) {
			minutes -= 60;
			hours++;
		}
		while (hours > 23) {
			hours -= 24;
			days++;
		}
		String ramingTime = "";
		if (end != -1) {
			if (days > 0) {
				ramingTime = "§e" + days + " §cTag(e), §e" + hours + " §cStunde(n), §e" + minutes
						+ " §cMinute(n) und §e" + seconds + " §cSekunde(n)";
			} else if (hours > 0) {
				ramingTime = "§e" + hours + " §cStunde(n), §e" + minutes + " §cMinute(n) und §e" + seconds
						+ " §cSekunde(n)";
			} else if (minutes > 0) {
				ramingTime = "§e" + minutes + " §cMinute(n) und §e" + seconds + " §cSekunde(n)";
			} else {
				ramingTime = "§e" + seconds + " §cSekunde(n)";
			}
		} else {
			ramingTime = "§4§lPERMANENT";
		}
		return ramingTime;
	}
	
	public boolean hashistory(UUID id, String reason) {
		ResultSet rs = BanSystemSpigot.mysql
				.getResult("SELECT UUID FROM `banhistory` WHERE UUID='" + id + "' AND Grund='" + reason + "'");
		try {
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void clearHistory(UUID uuid) {
		BanSystemSpigot.mysql.update("DELETE FROM banhistory WHERE UUID='" + uuid + "'");
	}
	
	public String getCreatedate(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT Erstelldatum FROM `banhistory` WHERE UUID='" + id + "'");
		try {
			while (rs.next()) {
				return rs.getString("Erstelldatum");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "§cNicht vorhanden!";
	}
	
	public boolean hashistory(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT UUID FROM `banhistory` WHERE UUID='" + id + "'");
		try {
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	
	public byte getLevel(UUID id, String reason) {
		ResultSet rs = BanSystemSpigot.mysql
				.getResult("SELECT UUID FROM `banhistory` WHERE UUID='" + id + "' AND Grund='" + reason + "'");
		byte i = 0;
		try {
			while (rs.next()) {
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public Type getType(UUID id, String reason) {
		ResultSet rs = BanSystemSpigot.mysql
				.getResult("SELECT Type FROM `ban` WHERE UUID='" + id + "' AND Grund='" + reason + "'");
		try {
			while (rs.next()) {
				return Type.valueOf(rs.getString("Type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean needUUDAndIP(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT UUID,IP FROM `ban` WHERE UUID='" + id + "'");
		try {
			while (rs.next()) {
				if (rs.getString("UUID") == null && rs.getString("IP") == null) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<InetAddress> getIPs() {
		ArrayList<InetAddress> IPs = new ArrayList<>();
		ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT IP FROM `ban`");
		try {
			while (rs.next()) {
				try {
					IPs.add(InetAddress.getByName(rs.getString("IP").replaceFirst("/", "")));
				} catch (UnknownHostException e) {
					continue;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return IPs;
	}
	
	public ArrayList<UUID> getBannedPlayers(InetAddress ip) {
		ArrayList<UUID> banned = new ArrayList<>();
		try {
			ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT UUID FROM `ban` WHERE IP='" + ip + "'");
			while (rs.next()) {
				banned.add(UUID.fromString(rs.getString("UUID")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return banned;
	}
	
	public boolean isBannedChat(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql
				.getResult("SELECT UUID FROM `ban` WHERE UUID='" + id + "' AND Type='" + Type.CHAT + "'");
		try {
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isBannedNetwork(UUID id) {
		ResultSet rs = BanSystemSpigot.mysql
				.getResult("SELECT UUID FROM `ban` WHERE UUID='" + id + "' AND Type='" + Type.NETWORK + "'");
		try {
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean needIP(UUID uniqueId) {
		ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT IP FROM ban WHERE UUID = '" + uniqueId + "'");
		try {
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setIP(UUID uniqueId, InetAddress address) {
		BanSystemSpigot.mysql
				.update("UPDATE ban SET IP = '" + address.getHostAddress() + "' WHERE UUID = '" + uniqueId + "'");
	}
	
	public String getID(UUID uniqueId, Type type) {
		ResultSet rs = BanSystemSpigot.mysql.getResult("SELECT ID FROM ban WHERE UUID = '" + uniqueId + "' and Type = '" + type + "'");
		try {
			while(rs.next()) {
				return rs.getString("ID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}