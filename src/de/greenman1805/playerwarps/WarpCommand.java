package de.greenman1805.playerwarps;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {

		if (cmd.getName().equalsIgnoreCase("pwarp")) {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player p = (Player) sender;
			
			if (args.length == 0) {
				sender.sendMessage("§7- §9/pwarp buy <beschreibung> §7- §rErstellt einen Warp mit Beschreibung für " + Main.moneyPerWarp + " Shards.");
				sender.sendMessage("§7- §9/pwarp list §7- §rListet deine Warps auf.");
				sender.sendMessage("§7- §9/pwarp delete <id> §7- §rLöscht deinen Warp, du bekommst den Preis erstattet.");
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("gui")) {
					WarpGui.open(p, 1);
				}

				if (args[0].equalsIgnoreCase("list")) {
					List<Warp> warps = Warp.getPlayerWarps(p.getUniqueId());
					p.sendMessage("§9Liste deiner Warps §f(" + warps.size() + " von " + Main.maxWarps + ")§9");
					for (Warp w : warps) {
						p.sendMessage("§7- §9ID: §f" + w.warpid + " §9Beschreibung: §f" + w.description);
					}
				}
				
				if (args[0].equalsIgnoreCase("buy")) {
					p.sendMessage(Main.prefix + "§f/pwarp buy <beschreibung>.");
				}
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("delete")) {
					List<Warp> warps = null;
					if (p.hasPermission("playerwarps.admin")) {
						warps = Warp.warps;
					} else {
						warps = Warp.getPlayerWarps(p.getUniqueId());
					}
					String Id = args[1];
					for (Warp w : warps) {
						if (String.valueOf(w.warpid).equalsIgnoreCase(Id)) {
							w.delete();
							if (p.hasPermission("playerwarps.admin")) {
								p.sendMessage(Main.prefix + "§aWarp entfernt.");
							} else {
								p.sendMessage(Main.prefix + "§aWarp entfernt und " + Main.moneyPerWarp + " Shards erstattet.");
								Main.econ.depositPlayer(p, Main.moneyPerWarp);
							}
							return true;
						}
					}
					p.sendMessage(Main.prefix + "§4Warp nicht gefunden.");
				}
			}

			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("buy")) {
					int warpCount = Warp.getPlayerWarpCount(p.getUniqueId());
					if (warpCount < Main.maxWarps || p.isOp()) {
						Location loc = p.getLocation();
						if (Warp.isLocationSafe(loc)) {
							int account_after = (int) (Main.econ.getBalance(p) - Main.moneyPerWarp);
							if (account_after >= 0) {
								Main.econ.withdrawPlayer(p, Main.moneyPerWarp);

								String desc = "";
								for (int i = 1; i < args.length; i++) {
									desc = desc + args[i] + " ";
								}
								desc.substring(0, desc.length() - 1);

								new Warp(p.getUniqueId(), loc, desc);
								p.sendMessage(Main.prefix + "§aWarp erstellt und " + Main.moneyPerWarp + " Shards von deinem Konto entfernt.");
							} else {
								p.sendMessage(Main.prefix + "§4Du hast nicht genug Geld!");
							}
						} else {
							p.sendMessage(Main.prefix + "§4Diese Location ist nicht sicher!");
						}
					} else {
						p.sendMessage(Main.prefix + "§4Du kannst keine weiteren Warps setzen.");
					}
				}
			}
		}

		return false;
	}

}
