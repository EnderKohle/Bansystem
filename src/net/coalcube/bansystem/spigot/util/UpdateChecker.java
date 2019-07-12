package net.coalcube.bansystem.spigot.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;

public class UpdateChecker {
    private int project;
    private URL checkURL;
    private String newVersion;
    private org.bukkit.plugin.Plugin pluginBukkit;
    private net.md_5.bungee.api.plugin.Plugin pluginBungee;

    public UpdateChecker(org.bukkit.plugin.Plugin plugin, int projectID) {
        this.pluginBukkit = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch(MalformedURLException e) {
            Bukkit.getLogger().warning("§4Could not connect to Spigotmc.org!");
        }
    }
    public UpdateChecker(net.md_5.bungee.api.plugin.Plugin plugin, int projectID) {
        this.pluginBungee = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch(MalformedURLException e) {
            Bukkit.getLogger().warning("§4Could not connect to Spigotmc.org!");
        }
    }
    public String getResourceUrl() {
    	return "https://spigotmc.org/resources/" + project;
    }
    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        if(pluginBukkit != null) {
        	return !pluginBukkit.getDescription().getVersion().equals(newVersion);
        } else 
        	return !pluginBungee.getDescription().getVersion().equals(newVersion);
    }
}
