package de.greenman1805.playerwarps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	public static Economy econ = null;
	public static List<Material> MaterialBlacklist = new ArrayList<Material>();
	public static YamlConfiguration data;
	public static String prefix = "§8[§9Warps§8] ";

	public static int maxWarps = 4;
	public static int moneyPerWarp = 3000;

	@Override
	public void onEnable() {
		if (!setupEconomy()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		checkFiles();
		registerCommands("pwarp", new WarpCommand());
		getServer().getPluginManager().registerEvents(new WarpGui(), this);

		MaterialBlacklist.add(Material.LAVA);
		MaterialBlacklist.add(Material.WATER);
	}

	@Override
	public void onDisable() {
		saveWarps();
	}

	public void registerCommands(String cmd, CommandExecutor exe) {
		getCommand(cmd).setExecutor(exe);
	}

	private void checkFiles() {
		File file1 = new File("plugins//PlayerWarps");
		File file2 = new File("plugins//PlayerWarps//warps.yml");

		if (!file1.isDirectory()) {
			file1.mkdir();
		}
		if (!file2.exists()) {
			try {
				file2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		loadWarps();
	}

	public static void loadWarps() {
		File file = new File("plugins//PlayerWarps//warps.yml");
		data = YamlConfiguration.loadConfiguration(file);
		Warp.globalid = data.getInt("GlobalWarpId");

		if (!data.isConfigurationSection("Warps")) {
			return;
		}
		for (String id : data.getConfigurationSection("Warps").getKeys(false)) {
			int i = Integer.parseInt(id);
			String locationString = data.getString("Warps." + i + ".location");
			new Warp(i, UUID.fromString(data.getString("Warps." + i + ".uuid")), Warp.getLocationFromString(locationString), data.getString("Warps." + i + ".description"));
		}
	}

	public static void saveWarps() {
		File file = new File("plugins//PlayerWarps//warps.yml");
		data.set("GlobalWarpId", Warp.globalid);
		data.set("Warps", null);
		for (Warp w : Warp.warps) {
			data.set("Warps." + w.warpid + ".uuid", w.uuid.toString());
			data.set("Warps." + w.warpid + ".description", w.description);
			data.set("Warps." + w.warpid + ".location", w.locationToString());
		}

		try {
			data.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
}
