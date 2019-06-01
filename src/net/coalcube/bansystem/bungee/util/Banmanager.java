package net.coalcube.bansystem.bungee.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import net.coalcube.bansystem.bungee.BanSystem;
import net.coalcube.bansystem.core.util.Type;
import net.coalcube.bansystem.core.util.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;

public class Banmanager {

	public void ban(String grund, long seconds, String ersteller, Type type, UUID uuid, InetAddress ip) {
		long end;
		long current = System.currentTimeMillis();
		if (seconds == -1) {
			end = -1;
		} else {
			long millis = seconds * 1000;
			end = current + millis;
		}
		try {
			if (ip != null)
				ip = InetAddress.getByName(ip.toString().replaceAll("/", ""));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		String zeitstempel = simpleDateFormat.format(date);
		BanSystem.mysql.update("INSERT INTO ban (UUID, Ende, Grund, Ersteller, IP, Type) VALUES ('" + uuid + "','" + end
				+ "','" + grund + "','" + ersteller + "','" + ip + "','" + type + "')");
		if (ip != null) {
			BanSystem.mysql
					.update("INSERT INTO `banhistory` (UUID, Ende, Grund, Ersteller, Erstelldatum, IP, Type, duration) "
							+ "VALUES ('" + uuid + "'," + "'" + end + "'," + "'" + grund + "'," + "'" + ersteller + "',"
							+ "'"
							+ zeitstempel
							+ "'," + "'" + ip.getHostAddress() + "'," + "'" + type + "', '" + seconds + "');");
		} else
			BanSystem.mysql
					.update("INSERT INTO `banhistory` (UUID, Ende, Grund, Ersteller, Erstelldatum, Type, duration) "
							+ "VALUES ('" + uuid + "'," + "'" + end + "'," + "'" + grund + "'," + "'" + ersteller + "',"
							+ "'"
							+ zeitstempel
							+ "'," + "'" + type + "', '" + seconds + "');");
	}

	public void ban(UUID uuid, int id, String ersteller, InetAddress ip) {
		String reason = "";
		byte lvl;
		Type type = null;
		long dauer = 0;
		for (String key : BanSystem.config.getSection("IDs").getKeys()) {
			if (id == Integer.parseInt(key)) {
				reason = BanSystem.config.getString("IDs." + key + ".reason");
				type = Type.valueOf(BanSystem.config.getString("IDs." + key + ".type"));
				if (hashistory(uuid, reason)) {
					lvl = (byte) (getLevel(uuid, reason) + 1);
				} else {
					lvl = 1;
				}
				for (String lvlkey : BanSystem.config.getSection("IDs." + key + ".lvl").getKeys()) {
					if ((byte) Byte.valueOf(lvlkey) == lvl) {
						dauer = BanSystem.config.getLong("IDs." + key + ".lvl." + lvlkey + ".duration");
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
		BanSystem.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.NETWORK + "'");
	}

	public void unmute(UUID id) {
		BanSystem.mysql.update("DELETE FROM ban WHERE UUID='" + id + "' AND Type='" + Type.CHAT + "'");
	}

	public boolean isbanned(UUID id) {
		ResultSet rs = BanSystem.mysql.getResult("SELECT UUID FROM `ban` WHERE UUID='" + id + "'");
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
		ResultSet rs = BanSystem.mysql
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
		ResultSet rs = BanSystem.mysql
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
			ResultSet rs = BanSystem.mysql
					.getResult("SELECT Ende FROM `ban` WHERE UUID='" + id + "' AND Grund='" + Grund + "'");
			while (rs.next()) {
				return rs.getLong("Ende");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getBanner(UUID id) {
		try {
			ResultSet rs = BanSystem.mysql.getResult("SELECT Ersteller FROM `ban` WHERE UUID='" + id + "'");
			while (rs.next()) {
				return rs.getString("Ersteller");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "unbekannt";
	}

	@SuppressWarnings("deprecation")
	public void sendHistorys(UUID id, CommandSender sender) {
		ResultSet rs = BanSystem.mysql.getResult(
				"SELECT * FROM `banhistory` WHERE UUID='" + id + "'");
		sender.sendMessage(BanSystem.PREFIX + "§8§m-------§8» §e" + UUIDFetcher.getName(id) + " §8«§m-------");

		if (hashistory2(id)) {
			try {
				while (rs.next()) {
					sender.sendMessage(BanSystem.PREFIX);
					sender.sendMessage(BanSystem.PREFIX + "§7Grund §8» §c" + rs.getString("Grund"));
					sender.sendMessage(BanSystem.PREFIX + "§7Erstelldatum §8» §c" + rs.getString("Erstelldatum"));
					sender.sendMessage(BanSystem.PREFIX + "§7Enddatum §8» §c"+new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(rs.getLong("Ende"))));
					sender.sendMessage(BanSystem.PREFIX + "§7Ersteller §8» §c" + rs.getString("Ersteller"));
					sender.sendMessage(BanSystem.PREFIX + "§7IP §8» §c" + rs.getString("IP"));
					sender.sendMessage(BanSystem.PREFIX + "§7Type §8» §c" + rs.getString("Type"));

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

					sender.sendMessage(BanSystem.PREFIX + "§7Dauer §8» §c" + ramingTime);
					sender.sendMessage(BanSystem.PREFIX);
					sender.sendMessage(BanSystem.PREFIX + "§8§m------------------------");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			sender.sendMessage(BanSystem.messages.getString("History.historynotfound"));
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
		ResultSet rs = BanSystem.mysql
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
		BanSystem.mysql.update("DELETE FROM banhistory WHERE UUID='" + uuid + "'");
	}

	public String getCreatedate(UUID id) {
		ResultSet rs = BanSystem.mysql.getResult("SELECT Erstelldatum FROM `banhistory` WHERE UUID='" + id + "'");
		try {
			while (rs.next()) {
				return rs.getString("Erstelldatum");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "§cNicht vorhanden!";
	}

	public boolean hashistory2(UUID id) {
		ResultSet rs = BanSystem.mysql.getResult("SELECT UUID FROM `banhistory` WHERE UUID='" + id + "'");
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
		ResultSet rs = BanSystem.mysql
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
		ResultSet rs = BanSystem.mysql
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
		ResultSet rs = BanSystem.mysql.getResult("SELECT UUID,IP FROM `ban` WHERE UUID='" + id + "'");
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
		ResultSet rs = BanSystem.mysql.getResult("SELECT IP FROM `ban`");
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
			ResultSet rs = BanSystem.mysql.getResult("SELECT UUID FROM `ban` WHERE IP='" + ip + "'");
			while (rs.next()) {
				banned.add(UUID.fromString(rs.getString("UUID")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return banned;
	}

	public boolean isBannedChat(UUID id) {
		ResultSet rs = BanSystem.mysql
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
		ResultSet rs = BanSystem.mysql
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
		ResultSet rs = BanSystem.mysql.getResult("SELECT IP FROM ban WHERE UUID = '" + uniqueId + "'");
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
		BanSystem.mysql
				.update("UPDATE ban SET IP = '" + address.getHostAddress() + "' WHERE UUID = '" + uniqueId + "'");
	}
}
