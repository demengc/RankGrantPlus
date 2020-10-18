package dev.demeng.rgp.menu;

import dev.demeng.demlib.Common;
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

public class DurationChooseInv extends Menu {

  private final RankGrantPlus i;
  private Duration currentDuration;

  DurationChooseInv(RankGrantPlus i, OfflinePlayer target, Player op, String rank) {
    super(
        54,
        Objects.requireNonNull(i.getMessages().getString("gui-names.choose-time"))
            .replace("%target%", Objects.requireNonNull(target.getName())));

    this.i = i;

    final String[] times =
        new String[] {
          "add-second",
          "add-minute",
          "add-hour",
          "add-day",
          "add-week",
          "subtract-second",
          "subtract-minute",
          "subtract-hour",
          "subtract-day",
          "subtract-week",
          "permanent"
        };

    for (String duration : times) {

      final String path = "duration." + duration + ".";

      this.currentDuration = new Duration(0L);

      Common.repeatTask(
          () -> {
            final List<String> continueLore = new ArrayList<>();
            for (String lore : i.getSettings().getStringList("duration.continue.lore")) {
              continueLore.add(setPlaceholders(lore, rank, target));
            }

            setItem(
                i.getSettings().getInt("duration.continue.slot") - 1,
                ItemCreator.quickBuild(
                    XMaterial.valueOf(i.getSettings().getString("duration.continue.item"))
                        .parseItem(),
                    Objects.requireNonNull(i.getSettings().getString("duration.continue.name")),
                    continueLore),
                event -> {
                  if (currentDuration.getTotalSeconds() == 0) {
                    return;
                  }

                  new ReasonSelectInv(i, target, op, rank, currentDuration);
                });

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
                event -> {
                  if (duration.startsWith("add-")) {
                    if (duration.endsWith("week")) addSeconds(604800L);
                    if (duration.endsWith("day")) addSeconds(86400L);
                    if (duration.endsWith("hour")) addSeconds(3600L);
                    if (duration.endsWith("minute")) addSeconds(60L);
                    if (duration.endsWith("second")) addSeconds(1L);
                    return;
                  }

                  if (duration.startsWith("subtract-")) {
                    if (duration.endsWith("week")) addSeconds(-604800L);
                    if (duration.endsWith("day")) addSeconds(-86400L);
                    if (duration.endsWith("hour")) addSeconds(-3600L);
                    if (duration.endsWith("minute")) addSeconds(-60L);
                    if (duration.endsWith("second")) addSeconds(-1L);
                  }

                  if (duration.equals("permanent")) {
                    currentDuration = new Duration(-1);
                    new ReasonSelectInv(i, target, op, rank, currentDuration);
                  }
                });
          },
          5L,
          5L,
          true);
    }

    open(op);
  }

  private long durationInSeconds = 0;

  private void addSeconds(long seconds) {

    if (seconds < 0 && durationInSeconds + seconds < 0) {
      durationInSeconds = 0;
      currentDuration = new Duration(0);
      return;
    }

    durationInSeconds = durationInSeconds + seconds;
    currentDuration = new Duration(durationInSeconds);
  }

  private String setPlaceholders(String s, String rank, OfflinePlayer target) {

    String rankName;

    if (i.getRanks().getString("ranks." + rank + ".name") == null) {
      rankName = rank;
    } else {
      rankName =
          MessageUtils.colorAndStrip(
              Objects.requireNonNull(i.getRanks().getString("ranks." + rank + ".name")));
    }

    final String duration;

    if (currentDuration.isPermanent()) {
      duration = i.getSettings().getString("duration.word-permanent");

    } else {
      duration =
          currentDuration.replaceTimes(
              Objects.requireNonNull(i.getSettings().getString("duration.duration-format")));
    }

    return s.replace("%rank%", rankName)
        .replace("%target%", Objects.requireNonNull(target.getName()))
        .replace("%duration%", MessageUtils.colorize(Objects.requireNonNull(duration)));
  }
}
