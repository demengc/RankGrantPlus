package dev.demeng.rankgrantplus.menus;

import dev.demeng.demlib.api.items.ItemBuilder;
import dev.demeng.demlib.api.items.XMaterial;
import dev.demeng.demlib.api.menus.CustomMenu;
import dev.demeng.demlib.api.messages.MessageUtils;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.utils.Duration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class ConfirmationInv {

  private final RankGrantPlus i;
  private final Duration duration;

  ConfirmationInv(
      RankGrantPlus i,
      OfflinePlayer target,
      Player op,
      String rank,
      Duration duration,
      String displayReason) {

    this.i = i;
    this.duration = duration;

    final CustomMenu menu =
        new CustomMenu(
            27,
            i.getMessages()
                .getString("gui-names.confirm-grant")
                .replace("%target%", target.getName()));

    final List<String> confirmLore = new ArrayList<>();
    for (String lore : i.getSettings().getStringList("confirmation.confirm.lore")) {
      confirmLore.add(setPlaceholders(lore, rank, target, displayReason, op));
    }

    menu.setItem(
        11,
        ItemBuilder.build(
            XMaterial.valueOf(i.getSettings().getString("confirmation.confirm.item")).parseItem(),
            i.getSettings().getString("confirmation.confirm.name"),
            confirmLore),
        event -> {
          op.closeInventory();
          if (grant(target, rank, duration, displayReason, op)) {
            MessageUtils.tell(
                op,
                setPlaceholders(
                    i.getSettings().getString("confirmation.confirm.message"),
                    rank,
                    target,
                    displayReason,
                    op));
          }
        });

    final List<String> cancelLore = new ArrayList<>();
    for (String lore : i.getSettings().getStringList("confirmation.cancel.lore")) {
      cancelLore.add(setPlaceholders(lore, rank, target, displayReason, op));
    }

    menu.setItem(
        15,
        ItemBuilder.build(
            XMaterial.valueOf(i.getSettings().getString("confirmation.cancel.item")).parseItem(),
            i.getSettings().getString("confirmation.cancel.name"),
            cancelLore),
        event -> {
          op.closeInventory();
          MessageUtils.tell(
              op,
              setPlaceholders(
                  i.getSettings().getString("confirmation.cancel.message"),
                  rank,
                  target,
                  displayReason,
                  op));
        });

    menu.open(op);
  }

  private boolean grant(
      OfflinePlayer target, String rank, Duration utils, String displayReason, Player op) {
    try {

      for (String cmd : i.getSettings().getStringList("commands.grant"))
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            cmd.replace("%target%", target.getName()).replace("%rank%", rank));

      i.getGrantLogs()
          .log(
              MessageUtils.colorAndStrip(
                  setPlaceholders(
                      i.getMessages().getString("log-format"), rank, target, displayReason, op)));

      if (!utils.isPermanent()) {
        i.getData()
            .createSection(target.getUniqueId() + "," + rank)
            .set("remaining", duration.getTotalSeconds());
        i.dataFile.saveConfig();
      }

      final String message = i.getMessages().getString("notification");
      if (!message.equals("none") && target.isOnline())
        MessageUtils.tell(
            (Player) target, setPlaceholders(message, rank, target, displayReason, op));

      return true;

    } catch (final Exception ex) {
      ex.printStackTrace();
      MessageUtils.tell(op, i.getMessages().getString("failed-grant"));
      return false;
    }
  }

  private String setPlaceholders(
      String s, String rank, OfflinePlayer target, String displayReason, Player op) {

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
        .replace("%duration%", MessageUtils.colorize(this.duration.replaceTimes(duration)))
        .replace("%reason%", MessageUtils.colorAndStrip(displayReason))
        .replace("%op%", op.getName());
  }
}
