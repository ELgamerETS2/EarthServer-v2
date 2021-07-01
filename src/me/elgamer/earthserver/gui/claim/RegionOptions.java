package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.MemberData;
import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class RegionOptions {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Region";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		if (OwnerData.isOwner(u.uuid, u.region_name)) {

			if (MemberData.hasMember(u.region_name)) {
				Utils.createItem(inv, Material.MAGENTA_GLAZED_TERRACOTTA, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Transfer Ownership",
						Utils.chat("&fClick to open the transfer ownership menu."),
						Utils.chat("&fYou can transfer ownership to any member of this region."),
						Utils.chat("&fOnce you have transferred ownership you will remain a member of the region."));
			}

			Utils.createItem(inv, Material.BARRIER, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Leave Region",
					Utils.chat("&fClick to leave the region."),
					Utils.chat("&fThe most recent member will take over ownership."),
					Utils.chat("&fIf there are no members then it can be claimed by anyone."));


			if (RegionData.isPublic(u.region_name)) {
				Utils.createItem(inv, Material.IRON_DOOR, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Private Region",
						Utils.chat("&fClick to make the region private."),
						Utils.chat("&fA private region is the default region."),
						Utils.chat("&fAll new members need to be accepted by the region owner."));
			} else {
				Utils.createItem(inv, Material.WOODEN_DOOR, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Public Region",
						Utils.chat("&fClick to make the region public."),
						Utils.chat("&fA public region implies that new members can join"),
						Utils.chat("&fwithout needing approval from the region owner."));
			}

		} else {

			Utils.createItem(inv, Material.BARRIER, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Leave Region",
					Utils.chat("&fClick to leave the region."));
		}

		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the region menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR)) {

			u.p.closeInventory();
			u.p.openInventory(RegionList.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Leave Region")) {

			u.p.closeInventory();
			
			if (OwnerData.isOwner(u.uuid, u.region_name)) {
				
				RegionLogs.closeLog(u.region_name,u.uuid);
				OwnerData.addNewOwner(u.region_name);
				OwnerData.removeOwner(u.uuid, u.region_name);
				WorldGuardFunctions.removeMember(u.region_name, u.uuid);
				
			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Transfer Ownership")) {

			u.p.closeInventory();
			u.p.openInventory(TransferOwnerGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Private Region")) {

			RegionData.setPrivate(u.current_region);

			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now private!");


		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Public Region")) {

			RegionData.setPublic(u.current_region);

			u.p.closeInventory();
			u.p.sendMessage(ChatColor.GREEN + "The region " + u.current_region + " is now public!");

		} else {


		}
	}

}