package de.greenman1805.playerwarps;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Warp {
	public static List<Warp> warps = new ArrayList<Warp>();
	public static int globalid = 1;
	int warpid;
	UUID uuid;
	ItemStack head;
	Location loc;
	String description;

	public Warp(int id, UUID uuid, Location loc, String description) {
		warpid = id;
		this.uuid = uuid;
		this.loc = loc;
		this.description = description;
		createHead();
		warps.add(this);
	}

	public Warp(UUID uuid, Location loc, String description) {
		warpid = globalid;
		globalid++;
		this.uuid = uuid;
		this.loc = loc;
		this.description = description;
		createHead();
		warps.add(this);
		Main.saveWarps();
	}

	private void createHead() {
		String playername = Bukkit.getOfflinePlayer(uuid).getName();
		head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setDisplayName("§a" + playername);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§r" + description);
		skull.setLore(lore);
		skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		head.setItemMeta(skull);
	}

	public static List<Warp> getPlayerWarps(UUID uuid) {
		List<Warp> list = new ArrayList<Warp>();
		for (Warp w : warps) {
			if (w.uuid.equals(uuid)) {
				list.add(w);
			}
		}
		return list;
	}

	public void delete() {
		warps.remove(this);
		Main.saveWarps();
	}

	public static int getPlayerWarpCount(UUID uuid) {
		int count = 0;
		for (Warp w : warps) {
			if (w.uuid.equals(uuid)) {
				count++;
			}
		}
		return count;
	}

	public static boolean isLocationSafe(Location loc) {
		Location copy = loc.clone();
		if (Main.MaterialBlacklist.contains(copy.getBlock().getType())) {
			return false;
		}
		copy.subtract(0, 1, 0);
		if (Main.MaterialBlacklist.contains(copy.getBlock().getType()) || copy.getBlock().getType() == Material.AIR) {
			return false;
		}

		return true;
	}

	public String locationToString() {
		String world = loc.getWorld().getName();
		String x = loc.getX() + "";
		String y = loc.getY() + "";
		String z = loc.getZ() + "";
		String pitch = loc.getPitch() + "";
		String yaw = loc.getYaw() + "";
		String location = world + "_" + x + "_" + y + "_" + z + "_" + pitch + "_" + yaw;
		return location;
	}

	public static Location getLocationFromString(String locationString) {
		String[] splitted = locationString.split("_");
		return new Location(Bukkit.getWorld(splitted[0]), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
	}

}
