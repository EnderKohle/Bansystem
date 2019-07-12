package net.coalcube.bansystem.bungee.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class UpdateChecker {
    private int project;
    private URL checkURL;
    private String newVersion;
    private Plugin plugin;

    public UpdateChecker(Plugin plugin, int projectID) {
        this.plugin = plugin;
        project = projectID;
        newVersion = plugin.getDescription().getVersion();
        try {
            checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch(MalformedURLException e) {
            ProxyServer.getInstance().getLogger().warning("§4Could not connect to Spigotmc.org!");
        }
    }
    public String getResourceUrl() {
    	return "https://spigotmc.org/resources/" + project;
    }
    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equals(newVersion);
    }
}
