package me.elgamer.earthserver.gui.claim;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.sql.OwnerData;
import me.elgamer.earthserver.sql.RegionData;
import me.elgamer.earthserver.sql.RegionLogs;
import me.elgamer.earthserver.utils.User;
import me.elgamer.earthserver.utils.Utils;
import me.elgamer.earthserver.utils.WorldGuardFunctions;

public class EditMember {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Edit Member";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (User u) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.WOODEN_DOOR, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Remove Member",
				Utils.chat("&fClick to remove " + u.member_name + " from this region."));
		
		Utils.createItem(inv, Material.WOODEN_DOOR, 1, 22, ChatColor.AQUA + "" + ChatColor.BOLD + "Transfer Ownership",
				Utils.chat("&fClick to make " + u.member_name + " owner of this region."),
				Utils.chat("&fYou will lose ownership of the region,"),
				Utils.chat("&fhowever you will remain a member."));


		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return",
				Utils.chat("&fClick to go back to the region members menu."));


		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(User u, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getType().equals(Material.SPRUCE_DOOR)) {

			u.p.closeInventory();
			u.p.openInventory(MembersGui.GUI(u));

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Leave Region")) {

			u.p.closeInventory();

			if (OwnerData.isOwner(u.uuid, u.region_name)) {

				RegionLogs.closeLog(u.region_name,u.uuid);
				OwnerData.addNewOwner(u.region_name);
				OwnerData.removeOwner(u.uuid, u.region_name);
				WorldGuardFunctions.removeMember(u.region_name, u.uuid);

			}

		} else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Members")) {

			u.gui_page = 1;
			u.p.closeInventory();
			u.p.openInventory(MembersGui.GUI(u));

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