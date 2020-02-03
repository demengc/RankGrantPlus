package com.demeng7215.rankgrantplus.inventories;

import com.demeng7215.demlib.api.gui.CustomInventory;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import com.demeng7215.rankgrantplus.utils.XMaterial;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankSelectInv extends CustomInventory {

	private final RankGrantPlus i;

	public RankSelectInv(RankGrantPlus i, OfflinePlayer target, Player op) {
		super(i.getConfiguration().getInt("gui-size.ranks"), MessageUtils.colorize(i.getLang()
				.getString("gui-names.select-rank").replace("%target%", target.getName())));

		this.i = i;

		if (i.getRanks().getBoolean("auto-list-ranks")) {

			int slot = 0;

			for (String rank : i.getPermissions().getGroups()) {

				List<String> finalLore = new ArrayList<>();
				for (String lore : i.getRanks().getStringList("default-format.lore")) {
					finalLore.add(replaceInfo(lore, rank, target));
				}

				if (slot < 53)

					setItem(slot++,
							XMaterial.valueOf(i.getRanks().getString("default-format.item"))
									.parseItem(),
							replaceInfo(i.getRanks().getString("default-format.name"), rank, target),
							finalLore, player -> new DurationChooseInv(i, target, op, rank).open(op));
			}
		} else {

			List<Integer> slotsOccupied = new ArrayList<>();

			for (String rank : i.getRanks().getConfigurationSection("ranks").getKeys(false)) {

				String path = "ranks." + rank + ".";

				if (op.hasPermission(i.getRanks().getString(path + "permission"))) {

					List<String> finalLore = new ArrayList<>();
					for (String lore : i.getRanks().getStringList(path + "lore")) {
						finalLore.add(replaceInfo(lore, rank, target));
					}

					int slot = i.getRanks().getInt(path + "slot") - 1;

					if (slot <= 54 && !slotsOccupied.contains(slot)) {

						slotsOccupied.add(slot);

						setItem(slot,
								XMaterial.valueOf(i.getRanks().getString(path + "item"))
										.parseItem(),
								replaceInfo(i.getRanks().getString(path + "name"), rank, target),
								finalLore, player -> new DurationChooseInv(i, target, op, rank).open(op));
					} else {
						MessageUtils.console("&cYou have chosen to display 2 ranks in the same slot " +
								"or have a slot ID higher than 54. Please check your ranks.yml.");
					}
				}
			}
		}
	}

	private String replaceInfo(String s, String rank, OfflinePlayer target) {

		String rankName;

		if (i.getRanks().getString("ranks." + rank + ".name") == null) {
			rankName = rank;
		} else {
			rankName = RankGrantPlus.stripColorCodes(i.getRanks().getString("ranks." + rank + ".name"));
		}

		return s.replace("%rank%", rankName).replace("%target%", target.getName());
	}
}
