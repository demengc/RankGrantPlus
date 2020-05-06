package com.demeng7215.rankgrantplus.inventories;

import com.demeng7215.demlib.api.items.ItemBuilder;
import com.demeng7215.demlib.api.menus.CustomMenu;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import com.demeng7215.rankgrantplus.utils.XMaterial;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankSelectInv {

  private RankGrantPlus i;

  public RankSelectInv(RankGrantPlus i, OfflinePlayer target, Player op) {

    this.i = i;

    final CustomMenu menu =
        new CustomMenu(
            i.getSettings().getInt("gui-size.ranks"),
            i.getMessages()
                .getString("gui-names.select-rank")
                .replace("%target%", target.getName()));

    if (i.getRanks().getBoolean("auto-list-ranks")) {

      int slot = 0;

      for (String rank : i.getPermission().getGroups()) {

        final List<String> finalLore = new ArrayList<>();
        for (String lore : i.getRanks().getStringList("default-format.lore")) {
          finalLore.add(setPlaceholders(lore, rank, target));
        }

        if (slot < 53) {

          menu.setItem(
              slot++,
              ItemBuilder.build(
                  XMaterial.valueOf(i.getRanks().getString("default-format.item")).parseItem(),
                  setPlaceholders(i.getRanks().getString("default-format.name"), rank, target),
                  finalLore),
              event -> new DurationChooseInv(i, target, op, rank));
        }
      }

    } else {

      for (String rank : i.getRanks().getConfigurationSection("ranks").getKeys(false)) {

        String path = "ranks." + rank + ".";

        if (op.hasPermission(i.getRanks().getString(path + "permission"))) {

          final List<String> finalLore = new ArrayList<>();

          for (String lore : i.getRanks().getStringList(path + "lore")) {
            finalLore.add(setPlaceholders(lore, rank, target));
          }

          menu.setItem(
              i.getRanks().getInt(path + "slot") - 1,
              ItemBuilder.build(
                  XMaterial.valueOf(i.getRanks().getString(path + "item")).parseItem(),
                  setPlaceholders(i.getRanks().getString(path + "name"), rank, target),
                  finalLore),
              event -> new DurationChooseInv(i, target, op, rank));
        }
      }
    }

    menu.open(op);
  }

  private String setPlaceholders(String s, String rank, OfflinePlayer target) {

    final String rankName;

    if (i.getRanks().getString("ranks." + rank + ".name") == null) {
      rankName = rank;
    } else {
      rankName = MessageUtils.colorAndStrip(i.getRanks().getString("ranks." + rank + ".name"));
    }

    return s.replace("%rank%", rankName).replace("%target%", target.getName());
  }
}
