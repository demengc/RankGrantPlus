package dev.demeng.rgp.menu;

import dev.demeng.demlib.item.ItemCreator;
import dev.demeng.demlib.menu.Menu;
import dev.demeng.demlib.message.MessageUtils;
import dev.demeng.demlib.xseries.XMaterial;
import dev.demeng.rgp.RankGrantPlus;
import dev.demeng.rgp.model.Duration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ReasonSelectInv extends Menu {

  private final RankGrantPlus i;
  private final Duration duration;

  ReasonSelectInv(
      RankGrantPlus i, OfflinePlayer target, Player op, String rank, Duration duration) {
    super(
        i.getSettings().getInt("gui-size.reasons"),
        Objects.requireNonNull(i.getMessages().getString("gui-names.select-reason"))
            .replace("%target%", Objects.requireNonNull(target.getName())));

    this.i = i;
    this.duration = duration;

    for (String reason :
        Objects.requireNonNull(i.getSettings().getConfigurationSection("reasons")).getKeys(false)) {

      final String path = "reasons." + reason + ".";

      if (op.hasPermission(
          Objects.requireNonNull(i.getSettings().getString(path + "permission")))) {

        final List<String> finalLore = new ArrayList<>();
        for (String lore : i.getSettings().getStringList(path + "lore")) {
          finalLore.add(setPlaceholders(lore, rank, target));
        }

        setItem(
            i.getSettings().getInt(path + "slot") - 1,
            ItemCreator.quickBuild(
                XMaterial.valueOf(i.getSettings().getString(path + "item")).parseItem(),
                setPlaceholders(i.getSettings().getString(path + "name"), rank, target),
                finalLore),
            event ->
                new ConfirmationInv(
                    i, target, op, rank, duration, i.getSettings().getString(path + "name")));
      }
    }

    open(op);
  }

  private String setPlaceholders(String s, String rank, OfflinePlayer target) {

    final String rankName;

    if (i.getRanks().getString("ranks." + rank + ".name") == null) {
      rankName = rank;
    } else {
      rankName =
          MessageUtils.colorAndStrip(
              Objects.requireNonNull(i.getRanks().getString("ranks." + rank + ".name")));
    }

    final String duration;

    if (this.duration.isPermanent()) {
      duration = i.getSettings().getString("duration.word-permanent");
    } else {
      duration =
          this.duration.replaceTimes(
              Objects.requireNonNull(i.getSettings().getString("duration.duration-format")));
    }

    return s.replace("%rank%", rankName)
        .replace("%target%", Objects.requireNonNull(target.getName()))
        .replace("%duration%", MessageUtils.colorize(Objects.requireNonNull(duration)));
  }
}
