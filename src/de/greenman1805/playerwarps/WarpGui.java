package de.greenman1805.playerwarps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class WarpGui implements Listener {

	public static void open(Player p, int page) {
		Inventory inv = Bukkit.createInventory(null, 54, "§9Warps Seite:§f " + page);

		int start = 45 * (page - 1);
		int end = 45 * page;

		if (Warp.warps.size() < end) {
			end = Warp.warps.size();
		}

		int k = 0;
		for (int i = start; i < end; i++) {
			Warp w = Warp.warps.get(i);
			inv.setItem(k, w.head);
			k++;
		}

		ItemStack gap = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		setItemName(gap, "§f", null);
		for (int i = 45; i < 54; i++) {
			inv.setItem(i, gap);
		}

		ItemStack bookHelp = new ItemStack(Material.WRITTEN_BOOK);
		setItemName(bookHelp, "§aSpielerWarp Befehle", null);
		inv.setItem(49, bookHelp);

		if (doesPageExist(page + 1)) {
			inv.setItem(53, getRightArrow(page));
		}

		if (doesPageExist(page - 1)) {
			inv.setItem(45, getLeftArrow(page));
		}

		p.openInventory(inv);
	}

	@EventHandler
	public void clickedOnItem(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null) {
			String title = e.getInventory().getTitle();

			if (title.contains("§9Warps Seite:")) {
				if (e.getRawSlot() >= 0 && e.getRawSlot() <= 53) {
					int page = Integer.parseInt(title.split(" ")[2]);

					if (e.getCurrentItem().getType() != Material.AIR) {
						if (e.isLeftClick()) {

							if (e.getSlot() >= 0 && e.getSlot() <= 44) {
								int listId = (page - 1) * 45 + e.getRawSlot();
								Warp w = Warp.warps.get(listId);
								if (Warp.isLocationSafe(w.loc)) {
									p.teleport(w.loc);
								} else {
									p.sendMessage(Main.prefix + "§4Dieser Warp Punkt ist nicht sicher!");
									for (Player player : Bukkit.getOnlinePlayers()) {
										if (player.hasPermission("playerwarps.admin")) {
											player.sendMessage(Main.prefix + "§4Warp §r" + w.warpid + " §4ist nicht sicher! Bitte prüfen!");
										}
									}
								}

							}

							if (e.getSlot() == 53) {
								if (doesPageExist(page + 1)) {
									e.setCancelled(true);
									open(p, page + 1);
									return;
								}
							}

							if (e.getSlot() == 45) {
								if (doesPageExist(page - 1)) {
									e.setCancelled(true);
									open(p, page - 1);
									return;
								}
							}

							if (e.getSlot() == 49) {
								p.chat("/pwarp");
								p.closeInventory();
								return;
							}
						}
						if (e.isRightClick() && p.hasPermission("playerwarps.admin")) {
							if (e.getSlot() >= 0 && e.getSlot() <= 44) {
								int listId = (page - 1) * 45 + e.getRawSlot();
								Warp w = Warp.warps.get(listId);
								p.sendMessage(Main.prefix + "§9Id: §f" + w.warpid);

							}
						}
					}
					e.setCancelled(true);
				}

			}
		}
	}

	public static void setItemName(ItemStack item, String name, ArrayList<String> lore_list) {
		ItemMeta meta;
		meta = item.getItemMeta();
		meta.setLore(lore_list);
		meta.setDisplayName(name);
		item.setItemMeta(meta);
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getRightArrow(int page) {
		page += 1;
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setDisplayName("§9Seite " + page);
		skull.setOwner("MHF_ArrowRight");
		head.setItemMeta(skull);
		return head;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getLeftArrow(int page) {
		page -= 1;
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setDisplayName("§9Seite " + page);
		skull.setOwner("MHF_ArrowLeft");
		head.setItemMeta(skull);
		return head;
	}

	public static boolean doesPageExist(int page) {
		if (page <= 0) {
			return false;
		}

		double a = Warp.warps.size() / 44;
		a += 0.5;
		int b = (int) a + 1;

		if (page <= b) {
			return true;
		}

		return false;
	}
}
