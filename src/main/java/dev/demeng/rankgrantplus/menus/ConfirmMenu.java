/*
 * MIT License
 *
 * Copyright (c) 2021 Demeng Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.demeng.rankgrantplus.menus;

import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.TimeUtils;
import dev.demeng.pluginbase.TimeUtils.DurationFormatter;
import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.pluginbase.menu.model.MenuButton;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.util.ConfigMenu;
import dev.demeng.rankgrantplus.util.Utils;
import java.io.IOException;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The menu for confirming grant activation.
 */
public class ConfirmMenu extends ConfigMenu {

  private final RankGrantPlus i;
  private final Player issuer;
  private final OfflinePlayer target;
  private final String rank;
  private final long duration;
  private final Placeholders placeholders;

  ConfirmMenu(RankGrantPlus i, Player issuer, OfflinePlayer target, String rank,
      long duration, String reason) {
    super(i, "confirm", Placeholders.of("%target%",
        Objects.requireNonNull(target.getName(), "Target name is null")));

    this.i = i;
    this.issuer = issuer;
    this.target = target;
    this.rank = rank;
    this.duration = duration;
    this.placeholders = Placeholders
        .of("%issuer%", issuer.getName())
        .add("%target%", Objects.requireNonNull(target.getName()))
        .add("%rank%", Utils.getRankName(rank))
        .add("%duration%", Utils.formatDuration(duration))
        .add("%reason%", Utils.getReasonName(reason));

    addButton(MenuButton.fromConfig(Objects.requireNonNull(
            i.getSettings().getConfigurationSection("menus.confirm.confirm"),
            "Confirm button is null"), placeholders,
        event -> {
          grant();
          ChatUtils.tell(issuer, placeholders.set(i.getMessages().getString("grant-confirm")));
          issuer.closeInventory();
        }));

    addButton(MenuButton.fromConfig(Objects.requireNonNull(
            i.getSettings().getConfigurationSection("menus.confirm.cancel"),
            "Cancel button is null"), placeholders,
        event -> {
          ChatUtils.tell(issuer, placeholders.set(i.getMessages().getString("grant-cancel")));
          issuer.closeInventory();
        }));
  }

  private void grant() {

    final Placeholders cmdPlaceholders = placeholders.copy()
        .add("%rank%", rank)
        .add("%duration%", TimeUtils.formatDuration(DurationFormatter.CONCISE, duration * 1000));

    if (duration > 0) {
      i.getData().set("temp-grants" + "." + target.getUniqueId() + "," + rank,
          System.currentTimeMillis() + (duration * 1000));

      try {
        i.getDataFile().save();
      } catch (IOException ex) {
        Common.error(ex, "Failed to save data.", false, issuer);
        return;
      }
    }

    for (String command : i.getSettings().getStringList("commands.activation")) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmdPlaceholders.set(command));
    }

    ChatUtils.log(cmdPlaceholders.set(i.getMessages().getString("log-format")));

    if (target.isOnline()) {
      ChatUtils.tell((Player) target, placeholders.set(i.getMessages().getString("notification")));
    }
  }
}
