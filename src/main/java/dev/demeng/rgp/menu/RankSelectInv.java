package dev.demeng.rgp.menu;

import dev.demeng.demlib.item.ItemCreator;
import dev.demeng.demlib.menu.Menu;
import dev.demeng.demlib.message.MessageUtils;
import dev.demeng.demlib.xseries.XMaterial;
import dev.demeng.rgp.RankGrantPlus;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RankSelectInv extends Menu {

  private final RankGrantPlus i;

  public RankSelectInv(RankGrantPlus i, OfflinePlayer target, Player op) {
    super(
        i.getSettings().getInt("gui-size.ranks"),
        Objects.requireNonNull(i.getMessages().getString("gui-names.select-rank"))
            .replace("%target%", Objects.requireNonNull(target.getName())));

    this.i = i;

    if (i.getRanks().getBoolean("auto-list-ranks")) {

      int slot = 0;

      for (String rank : i.getPermission().getGroups()) {

        final List<String> finalLore = new ArrayList<>();
        for (String lore : i.getRanks().getStringList("default-format.lore")) {
          finalLore.add(setPlaceholders(lore, rank, target));
        }

        if (slot < 53) {

          setItem(
              slot++,
              ItemCreator.quickBuild(
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

          setItem(
              i.getRanks().getInt(path + "slot") - 1,
              ItemCreator.quickBuild(
                  XMaterial.valueOf(i.getRanks().getString(path + "item")).parseItem(),
                  setPlaceholders(i.getRanks().getString(path + "name"), rank, target),
                  finalLore),
              event -> new DurationChooseInv(i, target, op, rank));
        }
      }
    }

    open(op);
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
