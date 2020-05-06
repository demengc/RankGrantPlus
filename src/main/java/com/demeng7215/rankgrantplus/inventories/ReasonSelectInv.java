package com.demeng7215.rankgrantplus.inventories;

import com.demeng7215.demlib.api.items.ItemBuilder;
import com.demeng7215.demlib.api.menus.CustomMenu;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import com.demeng7215.rankgrantplus.utils.Duration;
import com.demeng7215.rankgrantplus.utils.XMaterial;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class ReasonSelectInv {

  private RankGrantPlus i;
  private Duration duration;

  ReasonSelectInv(
      RankGrantPlus i, OfflinePlayer target, Player op, String rank, Duration duration) {

    this.i = i;
    this.duration = duration;

    final CustomMenu menu =
        new CustomMenu(
            i.getSettings().getInt("gui-size.reasons"),
            i.getMessages()
                .getString("gui-names.select-reason")
                .replace("%target%", target.getName()));

    for (String reason : i.getSettings().getConfigurationSection("reasons").getKeys(false)) {

      final String path = "reasons." + reason + ".";

      if (op.hasPermission(i.getSettings().getString(path + "permission"))) {

        final List<String> finalLore = new ArrayList<>();
        for (String lore : i.getSettings().getStringList(path + "lore")) {
          finalLore.add(setPlaceholders(lore, rank, target));
        }

        menu.setItem(
            i.getSettings().getInt(path + "slot") - 1,
            ItemBuilder.build(
                XMaterial.valueOf(i.getSettings().getString(path + "item")).parseItem(),
                setPlaceholders(i.getSettings().getString(path + "name"), rank, target),
                finalLore),
            event ->
                new ConfirmationInv(
                    i, target, op, rank, duration, i.getSettings().getString(path + "name")));
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

    final String duration;

    if (this.duration.isPermanent()) {
      duration = i.getSettings().getString("duration.word-permanent");
    } else {
      duration = this.duration.replaceTimes(i.getSettings().getString("duration.duration-format"));
    }

    return s.replace("%rank%", rankName)
        .replace("%target%", target.getName())
        .replace("%duration%", MessageUtils.colorize(duration));
  }
}
