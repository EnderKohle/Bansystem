package net.coalcube.bansystem.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.mysql.jdbc.Messages;

import net.coalcube.bansystem.core.BanSystem;

public class MySQL {

	private String host;
	private int port;
	private String database;
	private String user;
	private String password;
	private Connection con;

	public MySQL(String host, int port, String database, String user, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;

		connect();
	}

	public void connect() {
		Config messages = BanSystem.getInstance().getMessages();
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
			BanSystem.getInstance().getConsole().sendMessage(messages.getString("Prefix")
					+ "§7Die Verbindung zur MySQL Datenbank wurde §eerfolgreich §7hergestellt.");
		} catch (SQLException ex) {
			BanSystem.getInstance().getConsole().sendMessage(messages.getString("Prefix")
					+ "§cDie Verbindung zur MySQL Datenbank konnte nicht hergestellt werden.");
			BanSystem.getInstance().getConsole().sendMessage(messages.getString("Prefix")
					+ "§cBitte überprüfe die Anmeldedaten für die datenbank in der §econifg.yml§c.");
		}

	}

	public void disconnect() {
		try {
			con.close();
			BanSystem.getInstance().getConsole()
					.sendMessage(Messages.getString("Prefix") + "§7Verbindung zur Datenbank §egetrennt§7.");
		} catch (SQLException ex) {
			BanSystem.getInstance().getConsole().sendMessage(
					Messages.getString("Prefix") + "§cVerbindung zur Datenbank konnte nicht getrennt werden.");
		}
	}

	public void update(String qry) {
		if (isConnected()) {
			new FutureTask<>(new Runnable() {

				PreparedStatement ps;

				@Override
				public void run() {
					try {
						ps = con.prepareStatement(qry);

						ps.executeUpdate();
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}, 1).run();
		} else {
			connect();
		}
	}

	public void updateWithBoolean(String qry, boolean value) {
		if (isConnected()) {
			new FutureTask<>(new Runnable() {

				PreparedStatement ps;

				@Override
				public void run() {
					try {
						ps = con.prepareStatement(qry);
						ps.setBoolean(1, value);

						ps.executeUpdate();
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}, 1).run();
		} else {
			connect();
		}
	}

	public ResultSet getResult(String qry) {
		if (isConnected()) {
			try {
				final FutureTask<ResultSet> task = new FutureTask<ResultSet>(new Callable<ResultSet>() {

					PreparedStatement ps;

					@Override
					public ResultSet call() throws Exception {
						ps = con.prepareStatement(qry);

						return ps.executeQuery();
					}
				});

				task.run();

				return task.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			connect();
		}

		return null;
	}

	public boolean isConnected() {
		return (con != null ? true : false);
	}

}